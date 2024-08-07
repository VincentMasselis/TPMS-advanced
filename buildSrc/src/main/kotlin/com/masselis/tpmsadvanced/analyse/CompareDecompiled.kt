package com.masselis.tpmsadvanced.analyse

import com.masselis.tpmsadvanced.analyse.Fraction.Companion.fraction
import jadx.api.JadxDecompiler
import jadx.api.JavaClass
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import kotlin.math.roundToInt

internal abstract class CompareDecompiled : DefaultTask() {

    @get:Input
    abstract val clear: Property<DecompilerService>

    @get:Input
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
        val clear = clear.get().decompiled
        val obfuscated = obfuscated.get().decompiled
        modules.get().forEach { module ->
            val clearClasses = clear.filterByPackage(module.packageWatchList)
            val obfuscatedClasses = obfuscated.filterByPackage(module.packageWatchList)
            val keepClasses = clearClasses.intersect(obfuscatedClasses)
            val percentObfuscated = 1 - (keepClasses.count().div(clearClasses.count().toFloat()))
            val expectedPercentage = module.minimalObfuscationPercentage
                ?: defaultObfuscationPercentage.get()
            if (percentObfuscated < expectedPercentage.float)
                throw GradleException(
                    "Module \"${module.projectPath}\" obfuscation is not sufficient. Expecting at" +
                            " least ${expectedPercentage.asPercent()}% but only" +
                            " ${percentObfuscated.fraction.asPercent()}% of classes are" +
                            " obfuscated. Sample of classes kept by R8:" +
                            " ${keepClasses.joinToString(limit = 50)}"
                )
        }
    }

    private fun JadxDecompiler.filterByPackage(packages: Set<Regex>) =
        filterByPackage(packages) { it.fullName }

    private fun <T : Any> JadxDecompiler.filterByPackage(
        packages: Set<Regex>,
        classTransformation: (JavaClass) -> T
    ) = classesWithInners
        .filter { `class` ->
            packages.any { watchedPackage -> `class`.`package`.matches(watchedPackage) }
        }
        .mapTo(mutableSetOf(), classTransformation)
        .toSet()

    private fun Fraction.asPercent() = float.times(100).roundToInt()
}