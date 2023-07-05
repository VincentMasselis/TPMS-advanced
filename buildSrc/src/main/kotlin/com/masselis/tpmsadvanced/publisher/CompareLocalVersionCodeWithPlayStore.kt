package com.masselis.tpmsadvanced.publisher

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

public abstract class CompareLocalVersionCodeWithPlayStore : DefaultTask(), ServiceHolder {

    @get:Input
    public abstract val currentVc: Property<Int>

    @get:Input
    public abstract val packageName: Property<String>

    @get:Input
    public abstract val track: Property<String>

    init {
        group = "publishing"
        description =
            "Ensure the artifact to be promoted by promoteArtifact will be generated from the current commit"
    }

    @TaskAction
    internal fun process() {
        val playStoreVc = androidPublisher
            .edits()
            .withEdit(packageName.get()) { edit ->
                tracks()
                    .get(packageName.get(), edit.id, "beta")
                    .execute()
                    .releases
                    .first()
                    .versionCodes
                    .first()
                    .toInt()
            }
        if (playStoreVc != currentVc.get())
            throw GradleException("Current commit version code (${currentVc.get()}) differs to the current in beta from the play store ($playStoreVc)")
    }
}