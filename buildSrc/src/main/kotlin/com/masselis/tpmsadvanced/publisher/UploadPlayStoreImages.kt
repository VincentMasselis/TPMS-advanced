package com.masselis.tpmsadvanced.publisher

import com.google.api.client.http.FileContent
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction

public abstract class UploadPlayStoreImages : DefaultTask(), ServiceHolder {

    @get:InputDirectory
    public abstract val screenshotDirectory: DirectoryProperty

    @get:Input
    public abstract val packageName: Property<String>

    init {
        group = "publishing"
        description = "Updates the play store's listing screnshot"
    }

    @TaskAction
    internal fun process() {
        androidPublisher
            .edits()
            .withEdit(packageName.get()) { edit ->
                screenshotDirectory
                    .asFileTree
                    .sortedBy { it.name }
                    .forEach { file ->
                        images()
                            .upload(
                                packageName.get(),
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