package com.masselis.tpmsadvanced.publisher

import com.google.api.client.http.FileContent
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getValue

public abstract class PromoteToMain : DefaultTask(), ServiceHolder {

    @get:Input
    public abstract val packageName: Property<String>

    @get:Input
    public abstract val currentVc: Property<Int>

    @get:InputDirectory
    public abstract val screenshotDirectory: DirectoryProperty

    init {
        group = "publishing"
    }

    @TaskAction
    internal fun process() {
        val packageName by packageName
        androidPublisher
            .edits()
            .withEdit(this, packageName) { edit ->
                tracks()
                    .get(packageName, edit.id, "beta")
                    .execute()
                    .releases
                    .first()
                    .also { betaTrack ->
                        // Check if the artifact to promote is equals to the current version code
                        betaTrack.versionCodes
                            .first()
                            .toInt()
                            .also { playStoreVc ->
                                if (playStoreVc != currentVc.get())
                                    throw GradleException("Current commit version code (${currentVc.get()}) differs to the current in beta from the play store ($playStoreVc)")
                            }
                    }
                    .also { betaTrack ->
                        // Take the atifact and release note from beta and push them in production
                        updateTrack(packageName, edit.id, "production") {
                            releases.first().apply {
                                versionCodes.set(0, betaTrack.versionCodes.first())
                                setReleaseNotes(betaTrack.releaseNotes)
                            }
                        }
                    }
                // Deletes listings images
                images()
                    .deleteall(packageName, edit.id, "en-US", "phoneScreenshots")
                    .execute()
                // Push new listings images
                screenshotDirectory
                    .asFileTree
                    .sortedBy { it.name }
                    .forEach { file ->
                        images()
                            .upload(
                                packageName,
                                edit.id,
                                "en-US",
                                "phoneScreenshots",
                                FileContent("image/png", file)
                            )
                            .execute()
                    }
            }
    }
}
