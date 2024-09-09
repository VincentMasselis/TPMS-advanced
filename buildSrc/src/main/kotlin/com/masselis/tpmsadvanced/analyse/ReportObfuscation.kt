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

internal abstract class ReportObfuscation : DefaultTask() {

    @get:Internal
    abstract val clear: Property<DecompilerService>

    @get:Internal
    abstract val obfuscated: Property<DecompilerService>

    @get:Input
    abstract val modulesExtension: SetProperty<LibraryExtension.Data>

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
        val appClearClasses = mutableListOf<String>()
        val appKeptClasses = mutableListOf<String>()
        modulesExtension.get()
            .map { module ->
                val clearClasses = clear.filterByPackage(module.packageWatchList)
                    .also(appClearClasses::addAll)
                val obfuscatedClasses = obfuscated.filterByPackage(module.packageWatchList)
                val keptClasses = clearClasses.intersect(obfuscatedClasses)
                    .also(appKeptClasses::addAll)
                JsonReport.Module(
                    module.projectPath,
                    if (clearClasses.isEmpty()) 1f.fraction
                    else (1 - (keptClasses.count().div(clearClasses.count().toFloat()))).fraction,
                    module.packageWatchList,
                    keptClasses
                )
            }
            .let { modules ->
                JsonReport(
                    if (appClearClasses.isEmpty()) 1f.fraction
                    else (1 - (appKeptClasses.count().div(appClearClasses.count().toFloat()))).fraction,
                    modules,
                )
            }
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