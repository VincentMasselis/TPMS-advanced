package com.masselis.tpmsadvanced.playstore.task

import com.google.api.client.http.FileContent
import com.masselis.tpmsadvanced.playstore.ServiceHolder
import com.masselis.tpmsadvanced.playstore.androidPublisher
import com.masselis.tpmsadvanced.playstore.updateTrack
import com.masselis.tpmsadvanced.playstore.valuesource.ReleaseNote
import com.masselis.tpmsadvanced.playstore.withEdit
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.from
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.provideDelegate
import javax.inject.Inject

internal abstract class PublishToPlayStore : DefaultTask(), ServiceHolder {

    @get:Inject
    protected abstract val providerFactory: ProviderFactory

    @get:Input
    abstract val track: Property<String>

    @get:Input
    abstract val packageName: Property<String>

    @get:Input
    abstract val versionName: Property<String>

    @get:InputFile
    abstract val releaseBundle: RegularFileProperty

    @get:InputDirectory
    abstract val releaseNotesDir: DirectoryProperty

    private val releaseNotes
        get() = providerFactory.from(ReleaseNote::class) {
            this.releaseNotesDir = this@PublishToPlayStore.releaseNotesDir
        }

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
                        updateTrack(packageName, edit.id, track.get()) {
                            releases.first().apply {
                                name = versionName.get()
                                releaseNotes
                                    .first { it.language == "en-US" }
                                    .setText(this@PublishToPlayStore.releaseNotes.get())
                                versionCodes[0] = versionCode
                            }
                        }
                    }
            }
    }
}