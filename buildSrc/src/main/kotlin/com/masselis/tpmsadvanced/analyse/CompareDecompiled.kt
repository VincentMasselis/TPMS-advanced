package com.masselis.tpmsadvanced.analyse

import com.masselis.tpmsadvanced.analyse.Fraction.Companion.fraction
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

internal abstract class CompareDecompiled : DefaultTask() {

    @get:Internal
    abstract val clear: Property<DecompilerService>

    @get:Internal
    abstract val obfuscated: Property<DecompilerService>

    @get:Input
    abstract val defaultObfuscationPercentage: Property<Fraction>

    @get:Input
    abstract val modules: SetProperty<WatcherExtension.Content>

    init {
        group = "verification"
    }

    @TaskAction
    fun process() {
        val clear = clear.map { it.decompiled }.get()
        val obfuscated = obfuscated.map { it.decompiled }.get()
        modules.get().forEach { module ->
            val clearClasses = clear.filterByPackage(module.packageWatchList)
            val obfuscatedClasses = obfuscated.filterByPackage(module.packageWatchList)
            val keptClasses = clearClasses.intersect(obfuscatedClasses)
            val percentObfuscated = 1 - (keptClasses.count().div(clearClasses.count().toFloat()))
            val expectedPercentage = module.minimalObfuscationPercentage
                ?: defaultObfuscationPercentage.get()
            if (percentObfuscated < expectedPercentage.float)
                throw GradleException(
                    "Module \"${module.projectPath}\" obfuscation is not sufficient. Expecting at" +
                            " least ${expectedPercentage.asPercent()}% but only" +
                            " ${percentObfuscated.fraction.asPercent()}% of classes are" +
                            " obfuscated. Sample of classes kept by R8:" +
                            " ${keptClasses.joinToString(limit = 50)}"
                )
        }
    }
}