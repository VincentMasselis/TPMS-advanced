package com.masselis.tpmsadvanced.ressource

import com.masselis.tpmsadvanced.ressource.MonitorResourceService.Params
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.OperationCompletionListener
import org.slf4j.LoggerFactory
import oshi.SystemInfo
import java.io.File
import java.lang.System.currentTimeMillis
import java.util.Locale.ROOT
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(DelicateCoroutinesApi::class)
internal abstract class MonitorResourceService :
    BuildService<Params>,
    AutoCloseable,
    OperationCompletionListener {

    internal interface Params : BuildServiceParameters {
        val outputCsv: RegularFileProperty
        val graphName: Property<String>
        val maxGraphPoints: Property<Short>
    }

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val startTs = currentTimeMillis()

    @Suppress("LoggingPlaceholderCountMatchesArgumentCount")
    private val job = GlobalScope.launch(IO) {
        runCatching {
            val csvFile = parameters.outputCsv.get().asFile.apply {
                if (exists()) delete()
                if (parentFile.exists().not()) parentFile.mkdirs()
                createNewFile()
                writeText("timestamp,cpu,mem\n")
            }
            var previousTick: LongArray
            with(SystemInfo().hardware.also { previousTick = it.processor.systemCpuLoadTicks }) {
                while (true) {
                    delay(1.seconds)
                    "%s,%.2f,%.2f\n"
                        .format(
                            ROOT,
                            (currentTimeMillis() - startTs).milliseconds,
                            processor.getSystemCpuLoadBetweenTicks(previousTick)
                                .also { previousTick = processor.systemCpuLoadTicks }
                                .let { if (it < 0) 0.0 else it * 100.0 },
                            memory.available
                                .toDouble()
                                .let { ((memory.total - it) / memory.total) * 100.0 }
                        )
                        .also(csvFile::appendText)
                }
            }
        }.onFailure {
            if (it !is CancellationException) {
                logger.error("An error occurred during the resource monitoring", it)
            }
        }
    }

    override fun close() {
        job.cancel()
        parameters
            .outputCsv
            .asFile
            .get()
            .readLines()
            .takeIf { it.size > 1 }
            ?.drop(1)
            ?.map { it.split(',') }
            ?.let {
                // Downsample points, no average between points here, this is not critical
                val step = (it.size / parameters.maxGraphPoints.get()).coerceAtLeast(1)
                it.filterIndexed { index, _ -> index % step == 0 }
            }
            ?.let { points ->
                Triple(
                    points.joinToString(
                        separator = ", ",
                        prefix = "[",
                        postfix = "]"
                        // Removes the milliseconds from the duration and keeps only a precision of
                        // a single second
                    ) { "\"${Duration.parse(it[0]).inWholeSeconds.seconds}\"".filterNot(Char::isWhitespace) },
                    points.map { (it[1].toFloat() * 10).roundToInt() / 10.0 },
                    points.map { (it[2].toFloat() * 10).roundToInt() / 10.0 },
                )
            }
            ?.let { (timesJson, cpuList, memList) ->
                """
                    ```mermaid
                    ---
                    config:
                      themeVariables:
                        xyChart:
                          plotColorPalette: '#4169E1, #808080'
                    ---
                    xychart
                        title "Resource Usage (%)"
                        x-axis $timesJson
                        y-axis "Usage %" 0 --> 100
                        line $cpuList
                        line $memList
                    ```
                """
            }
            ?.trimIndent()
            ?.also { markdownChart ->
                System
                    .getenv("GITHUB_STEP_SUMMARY")
                    ?.let(::File)
                    ?.takeIf { it.exists() }
                    ?.apply {
                        appendText("### 📊 Resource usage for tasks \"${parameters.graphName.get()}\"\n")
                        appendText("**Blue: CPU** | **Gray: Memory**\n\n")
                        appendText("$markdownChart\n")
                        appendText("Complete report is attached as a CSV file below, see the [artifacts section](#artifacts)")
                    }
            }
    }

    override fun onFinish(event: FinishEvent?) {
        // Only needed because the current class implements `OperationCompletionListener`
    }
}