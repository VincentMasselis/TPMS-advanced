package com.masselis.tpmsadvanced.publisher

import com.google.api.client.http.FileContent
import com.google.api.services.androidpublisher.model.LocalizedText
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.enterprise.test.FileProperty

public abstract class PushBundleToPlayStore : DefaultTask(), ServiceHolder {

    @get:InputFile
    public abstract val releaseBundle: RegularFileProperty

    @get:InputFile
    public abstract val releaseNotes: RegularFileProperty

    @get:Input
    public abstract val packageName: Property<String>

    @get:Input
    public abstract val track: Property<String>

    init {
        group = "publishing"
        description = "Push the bundle to the filled play store track"
    }

    @TaskAction
    internal fun process() = androidPublisher
        .edits()
        .withEdit(this, packageName.get()) { edit ->
            bundles()
                .upload(
                    packageName.get(),
                    edit.id,
                    FileContent(
                        "application/octet-stream",
                        releaseBundle.asFile.get()
                    )
                )
                .execute()
                .versionCode
                .toLong()
                .also { versionCode ->
                    updateTrack(packageName.get(), edit.id, track.get()) {
                        releases.first().apply {
                            releaseNotes
                                .first { it.language == "en-US" }
                                .setText(this@PushBundleToPlayStore.releaseNotes.get().asFile.readText())
                            versionCodes.set(0, versionCode)
                        }
                    }
                }
        }
}
