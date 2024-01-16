package com.masselis.tpmsadvanced.playstore

import com.google.api.client.http.FileContent
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getValue

public abstract class UpdatePlayStoreScreenshots : DefaultTask(), ServiceHolder {

    @get:InputDirectory
    public abstract val screenshotDirectory: DirectoryProperty

    @get:Input
    public abstract val packageName: Property<String>

    init {
        group = "publishing"
        description = "Updates the play store listing with the latest screenshots"
    }

    @TaskAction
    internal fun process() {
        val packageName by packageName
        androidPublisher
            .edits()
            .withEdit(this, packageName) { edit ->
                if (screenshotDirectory.asFileTree.isEmpty)
                    throw GradleException("Screenshot's directory is empty")

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