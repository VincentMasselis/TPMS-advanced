package com.masselis.tpmsadvanced.github.task

import CommitSha
import com.masselis.tpmsadvanced.github.valuesource.ReleaseNoteBetweenCommit
import org.gradle.kotlin.dsl.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.process.ExecOperations
import javax.inject.Inject

internal abstract class CreateRelease : DefaultTask() {

    @get:Inject
    protected abstract val providerFactory: ProviderFactory

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:Input
    abstract val githubToken: Property<String>

    @get:Input
    abstract val tagName: Property<String>

    @get:Input
    abstract val preRelease: Property<Boolean>

    @get:Input
    abstract val lastReleaseCommitSha: Property<String>

    @get:InputFiles
    abstract val assets: ConfigurableFileCollection

    private val releaseNotes
        get() = providerFactory.from(ReleaseNoteBetweenCommit::class) {
            fromCommitSha = lastReleaseCommitSha
            toCommitSha = providerFactory.from(CommitSha::class) { refname = "HEAD" }
        }

    init {
        group = "publishing"
        description = "Creates or updates a release on github"
    }

    @TaskAction
    internal fun process(): Any =
        // No release exists
        execOperations.exec {
            commandLine(
                "curl", "-L",
                "-X", "POST",
                "-H", "Accept: application/vnd.github+json",
                "-H", "Authorization: Bearer ${githubToken.get()}",
                "-H", "X-GitHub-Api-Version: 2022-11-28",
                "https://api.github.com/repos/VincentMasselis/TPMS-advanced/releases",
                "-d",
                JsonObject(
                    mapOf(
                        "tag_name" to JsonPrimitive(tagName.get()),
                        "body" to JsonPrimitive(releaseNotes.get()),
                        "prerelease" to JsonPrimitive(preRelease.get())
                    )
                )
            )
        }
}