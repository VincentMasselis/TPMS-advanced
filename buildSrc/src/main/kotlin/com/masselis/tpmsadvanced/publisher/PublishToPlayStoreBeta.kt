package com.masselis.tpmsadvanced.publisher

import com.google.api.client.http.FileContent
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.provideDelegate

internal abstract class PublishToPlayStoreBeta : DefaultTask(), ServiceHolder {

    @get:Input
    public abstract val packageName: Property<String>

    @get:InputFile
    public abstract val releaseBundle: RegularFileProperty

    @get:InputFile
    public abstract val releaseNotes: RegularFileProperty

    init {
        group = "publishing"
        description = "Push the bundle into the beta track"
    }

    @TaskAction
    internal fun process() {
        // Pushes bundle to the play store
        val packageName by packageName
        androidPublisher
            .edits()
            .withEdit(this, packageName) { edit ->
                bundles()
                    .upload(
                        packageName,
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
                        updateTrack(packageName, edit.id, "beta") {
                            releases.first().apply {
                                releaseNotes
                                    .first { it.language == "en-US" }
                                    .setText(this@PublishToPlayStoreBeta.releaseNotes.get().asFile.readText())
                                versionCodes.set(0, versionCode)
                            }
                        }
                    }
            }
    }
}