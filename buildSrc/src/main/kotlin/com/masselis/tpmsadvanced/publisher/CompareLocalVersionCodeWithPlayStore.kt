package com.masselis.tpmsadvanced.publisher

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.services.ServiceReference
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.jetbrains.kotlin.com.google.gson.Gson
import javax.inject.Inject

public abstract class CompareLocalVersionCodeWithPlayStore : DefaultTask() {

    @get:ServiceReference("android-publisher-service")
    internal abstract val service: Property<AndroidPublisherService>

    @get:Inject
    internal abstract val execOperations: ExecOperations

    @get:Input
    public abstract val currentVc: Property<Int>

    init {
        group = "publishing"
        description =
            "Ensure the artifact to be promoted by promoteArtifact will be generated from the current commit"
    }

    @TaskAction
    internal fun process() {
        val playStoreVc = service
            .get()
            .androidPublisher
            .edits()
            .run {
                withCommit(service.get().packageName) {
                    tracks()
                        .get(service.get().packageName, it.id, "beta")
                        .execute()
                        .releases
                        .first()
                        .versionCodes
                        .first()
                        .toInt()
                }
            }
        if (playStoreVc != currentVc.get())
            throw GradleException("Current commit version code (${currentVc.get()}) differs to the current in beta from the play store ($playStoreVc)")
    }

    internal companion object {
        private val gson = Gson()
    }
}