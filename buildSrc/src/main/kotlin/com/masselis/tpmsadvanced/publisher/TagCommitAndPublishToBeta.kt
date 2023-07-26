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
import org.gradle.process.ExecOperations
import javax.inject.Inject

public abstract class TagCommitAndPublishToBeta : DefaultTask(), ServiceHolder {

    @get:Input
    public abstract val packageName: Property<String>

    @get:Input
    public abstract val currentVc: Property<Int>

    @get:InputFile
    public abstract val releaseBundle: RegularFileProperty

    @get:InputFile
    public abstract val releaseNotes: RegularFileProperty

    @get:Inject
    internal abstract val execOperations: ExecOperations

    init {
        group = "publishing"
        description = "Tag the commit and push the bundle to the filled play store track"
    }

    @TaskAction
    internal fun process() {
        execOperations.exec {
            commandLine("git", "fetch")
            args("--all", "--tags")
        }
        execOperations.exec {
            // If the tag already exists, this step fails
            commandLine("git", "tag", "vc${currentVc.get()}")
        }
        execOperations.exec {
            commandLine("git", "push")
            args("--tags")
        }
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
                                    .setText(this@TagCommitAndPublishToBeta.releaseNotes.get().asFile.readText())
                                versionCodes.set(0, versionCode)
                            }
                        }
                    }
            }
    }
}
