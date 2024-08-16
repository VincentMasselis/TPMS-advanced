package com.masselis.tpmsadvanced.analyse

import com.masselis.tpmsadvanced.analyse.Fraction.Companion.fraction
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToStream
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

internal abstract class ReportModules : DefaultTask() {

    @get:Internal
    abstract val clear: Property<DecompilerService>

    @get:Internal
    abstract val obfuscated: Property<DecompilerService>

    @get:Input
    abstract val modules: SetProperty<WatcherExtension.Content>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        group = "verification"
    }

    @OptIn(ExperimentalSerializationApi::class)
    @TaskAction
    fun process() {
        val clear = clear.map { it.decompiled }.get()
        val obfuscated = obfuscated.map { it.decompiled }.get()
        modules
            .get()
            .map { module ->
                val clearClasses = clear.filterByPackage(module.packageWatchList)
                val obfuscatedClasses = obfuscated.filterByPackage(module.packageWatchList)
                val keptClasses = clearClasses.intersect(obfuscatedClasses)
                if (keptClasses.isEmpty()) {
                    println("Congrats ! Every class in \"${module.projectPath}\" is obfuscated !")
                } else {
                    StringBuilder()
                        .append("List of class kept in the final APK for the module \"${module.projectPath}\":\n  ּ  ")
                        .append(keptClasses.joinToString(limit = 1000, separator = "\n  ּ  "))
                        .also(::println)
                }
                JsonReport.Module(
                    module.projectPath,
                    (1 - (keptClasses.count().div(clearClasses.count().toFloat()))).fraction,
                    module.packageWatchList,
                    keptClasses
                )
            }
            .let(::JsonReport)
            .also { report ->
                outputFile
                    .get()
                    .asFile
                    .apply { if (exists().not()) createNewFile() }
                    .outputStream()
                    .use { json.encodeToStream(report, it) }
            }
            .also { println("Complete report saved here: ${outputFile.get()}") }
    }

    companion object {
        private val json = Json { prettyPrint = true }
    }
}