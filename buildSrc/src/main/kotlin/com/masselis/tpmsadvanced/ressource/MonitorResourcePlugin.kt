package com.masselis.tpmsadvanced.ressource

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.build.event.BuildEventsListenerRegistry
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.registerIfAbsent
import javax.inject.Inject

public abstract class MonitorResourcePlugin : Plugin<Project> {

    @get:Inject
    internal abstract val listenerRegistry: BuildEventsListenerRegistry

    override fun apply(target: Project): Unit = with(target) {
        if (target !== rootProject) {
            throw GradleException("This plugin must be applied at root level only")
        }
        gradle.sharedServices
            .registerIfAbsent(
                "monitor-resource-service", MonitorResourceService::class
            ) {
                parameters.outputCsv = gradle
                    .startParameter
                    .taskNames
                    .joinToString("_")
                    .replace("[^a-zA-Z0-9.\\-]".toRegex(), "_")
                    .let { layout.buildDirectory.file("reports/resources/$it.csv") }
                parameters.graphName = gradle
                    .startParameter
                    .taskRequests
                    .flatMap { it.args }
                    .joinToString(" ")
                parameters.maxGraphPoints = 12
            }
            // Keeps the service alive until the build finishes because the service reference is
            // hold by the build event registry
            .also(listenerRegistry::onTaskCompletion)
    }
}