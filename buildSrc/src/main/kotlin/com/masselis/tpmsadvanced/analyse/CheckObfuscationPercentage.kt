package com.masselis.tpmsadvanced.analyse

import com.masselis.tpmsadvanced.analyse.Fraction.Companion.fraction
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

internal abstract class CheckObfuscationPercentage : DefaultTask() {

    @get:Internal
    abstract val clear: Property<DecompilerService>

    @get:Internal
    abstract val obfuscated: Property<DecompilerService>

    @get:Input
    abstract val defaultModuleObfuscationPercentage: Property<Fraction>

    @get:Input
    abstract val appObfuscationPercentage: Property<Fraction>

    @get:Input
    abstract val modulesExtension: SetProperty<LibraryExtension.Data>

    init {
        group = "verification"
    }

    @TaskAction
    fun process() {
        val clear = clear.map { it.decompiled }.get()
        val obfuscated = obfuscated.map { it.decompiled }.get()
        val appClearClasses = mutableListOf<String>()
        val appKeptClasses = mutableListOf<String>()
        modulesExtension.get().forEach { module ->
            val clearClasses = clear.filterByPackage(module.packageWatchList)
                .also(appClearClasses::addAll)
            val obfuscatedClasses = obfuscated.filterByPackage(module.packageWatchList)
            val keptClasses = clearClasses.intersect(obfuscatedClasses)
                .also(appKeptClasses::addAll)
            val percentObfuscated =
                1 - (keptClasses.count().div(clearClasses.count().toFloat()))
            val expectedPercentage = module.minimalObfuscationPercentage
                ?: defaultModuleObfuscationPercentage.get()
            if (percentObfuscated < expectedPercentage.float)
                throw GradleException(
                    "Module \"${module.projectPath}\" obfuscation is not sufficient. Expecting at" +
                            " least ${expectedPercentage.asPercent()}% but only" +
                            " ${percentObfuscated.fraction.asPercent()}% of classes are" +
                            " obfuscated. Sample of classes kept by R8:" +
                            " ${keptClasses.joinToString(limit = 50)}"
                )
        }
        if (appClearClasses.isNotEmpty()) {
            val percentObfuscated =
                (1 - (appKeptClasses.count().div(appClearClasses.count().toFloat())))
            if (percentObfuscated < appObfuscationPercentage.get().float)
                throw GradleException(
                    "App obfuscation is not sufficient. Expecting at" +
                            " least ${appObfuscationPercentage.get().asPercent()}% but only" +
                            " ${percentObfuscated.fraction.asPercent()}% of classes are" +
                            " obfuscated. Sample of classes kept by R8:" +
                            " ${appKeptClasses.joinToString(limit = 50)}"
                )
        }
    }
}