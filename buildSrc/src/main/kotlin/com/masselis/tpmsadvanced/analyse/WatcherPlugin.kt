package com.masselis.tpmsadvanced.analyse

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import java.util.Optional

public class WatcherPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        val ext = extensions.create<WatcherExtension>("obfuscationAssertions", target.path)
        ext.packageWatchList.convention(provider { throw GradleException("A list of package to watch is missing to analyse apk obfuscation, please set a value using \"obfuscationAssertions.packageWatchList\"") })
        ext.minimalObfuscationPercentageOpt.convention(Optional.empty())
    }
}
