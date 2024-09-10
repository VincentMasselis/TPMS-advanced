package com.masselis.tpmsadvanced.analyse

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

@Suppress("OPT_IN_USAGE")
internal abstract class CheckObfuscationPercentage : DefaultTask() {

    @get:Input
    abstract val modulesExtension: SetProperty<LibraryExtension.Data>

    @get:Input
    abstract val defaultModuleObfuscationPercentage: Property<Fraction>

    @get:Input
    abstract val appObfuscationPercentage: Property<Fraction>

    @get:InputFile
    abstract val moduleReportFile: RegularFileProperty

    init {
        group = "verification"
    }

    @TaskAction
    fun process() {
        moduleReportFile
            .get()
            .asFile
            .inputStream()
            .use { Json.decodeFromStream<ModuleReport>(it) }
            .also { reports ->
                modulesExtension.get().forEach { ext ->
                    val report = reports.modules.first { it.path == ext.projectPath }
                    val expectedPercentage = ext.minimalObfuscationPercentage
                        ?: defaultModuleObfuscationPercentage.get()
                    if (report.obfuscationRate.float < expectedPercentage.float)
                        throw GradleException(
                            "Module \"${report.path}\" obfuscation is not sufficient. Expecting at" +
                                    " least ${expectedPercentage.asPercent()}% but only" +
                                    " ${report.obfuscationRate.asPercent()}% of classes are" +
                                    " obfuscated. Sample of classes kept by R8:" +
                                    " ${report.keptClasses.joinToString(limit = 50)}"
                        )
                }
                if (reports.globalObfuscationRate.float < appObfuscationPercentage.get().float)
                    throw GradleException(
                        "App obfuscation is not sufficient. Expecting at" +
                                " least ${appObfuscationPercentage.get().asPercent()}% but only" +
                                " ${reports.globalObfuscationRate.asPercent()}% of classes are" +
                                " obfuscated. Sample of classes kept by R8:" +
                                " ${reports.modules.flatMap { it.keptClasses }.joinToString(limit = 50)}"
                    )
            }
    }
}
