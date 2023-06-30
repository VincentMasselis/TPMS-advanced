package com.masselis.tpmsadvanced.publisher

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

public class AndroidPublisherPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val ext = project.extensions.create<AndroidPublisherExtension>("androidPublisher")
        project.gradle.sharedServices.registerIfAbsent(
            "android-publisher-service",
            AndroidPublisherService::class.java
        ) {
            parameters.serviceAccountCredentials.set(ext.serviceAccountCredentials)
        }
    }
}