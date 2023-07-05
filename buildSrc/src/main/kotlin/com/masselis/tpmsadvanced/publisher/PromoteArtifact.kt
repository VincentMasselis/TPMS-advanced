package com.masselis.tpmsadvanced.publisher

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

public abstract class PromoteArtifact : DefaultTask(), ServiceHolder {
    @get:Input
    public abstract val packageName: Property<String>

    @get:Input
    public abstract val fromTrack: Property<String>

    @get:Input
    public abstract val toTrack: Property<String>

    init {
        group = "publishing"
    }

    @TaskAction
    internal fun process() = androidPublisher
        .edits()
        .withEdit(this, packageName.get()) { edit ->
            tracks()
                .get(packageName.get(), edit.id, fromTrack.get())
                .execute()
                .releases
                .first()
                .let { fromTrackRelease ->
                    updateTrack(packageName.get(), edit.id, toTrack.get()) {
                        releases.first().apply {
                            versionCodes.set(0, fromTrackRelease.versionCodes.first())
                            setReleaseNotes(fromTrackRelease.releaseNotes)
                        }
                    }
                }
        }
}
