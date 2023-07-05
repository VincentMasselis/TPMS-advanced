package com.masselis.tpmsadvanced.publisher

import com.google.api.client.http.FileContent
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction

public abstract class PushBundleToPlayStore : DefaultTask(), ServiceHolder {

    @get:InputDirectory
    public abstract val releaseDir: DirectoryProperty

    @get:Input
    public abstract val packageName: Property<String>

    @get:Input
    public abstract val track: Property<String>

    init {
        group = "publishing"
        description = "Push the bundle to the filled play store track"
    }

    @TaskAction
    internal fun process() {
        androidPublisher
            .edits()
            //.withEdit()
            .withEdit(packageName.get()) { edit ->
                updateTrack(packageName.get(), edit.id, track.get()) {
                    bundles()
                        .upload(
                            packageName.get(),
                            edit.id,
                            FileContent(
                                "application/octet-stream",
                                releaseDir.get().asFileTree.first { it.extension == "aab" })
                        )
                        .execute()
                        .versionCode
                        .toLong()
                        .also { releases.first().versionCodes.set(0, it) }
                }
            }
    }
}
