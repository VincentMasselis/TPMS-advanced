package com.masselis.tpmsadvanced.github.task

import CommitSha
import SemanticVersion
import com.masselis.tpmsadvanced.github.valuesource.ReleaseNoteBetweenCommit
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.from
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class CreateRelease : DefaultTask() {

    @get:Inject
    protected abstract val providerFactory: ProviderFactory

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:Input
    abstract val githubToken: Property<String>

    @get:Input
    abstract val tagName: Property<SemanticVersion>

    @get:Input
    abstract val preRelease: Property<Boolean>

    @get:Input
    abstract val lastReleaseCommitSha: Property<String>

    @get:InputFiles
    abstract val assets: ConfigurableFileCollection

    private val releaseNotes
        get() = providerFactory.from(ReleaseNoteBetweenCommit::class) {
            fromCommitSha = lastReleaseCommitSha
            toCommitSha = providerFactory.from(CommitSha::class) { argument = "HEAD" }
        }

    init {
        group = "publishing"
        description = "Creates or updates a release on github"
    }

    @TaskAction
    internal fun process() {
        ByteArrayOutputStream()
            .also {
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
                                "tag_name" to JsonPrimitive(tagName.get().toString()),
                                "body" to JsonPrimitive(releaseNotes.get()),
                                "prerelease" to JsonPrimitive(preRelease.get())
                            )
                        )
                    )
                    standardOutput = it
                }
            }
            .use { it.toString() }
            .let { Json.decodeFromString<JsonObject>(it) }
            .getValue("id")
            .jsonPrimitive
            .int
            .also { releaseId ->
                assets.forEach { file ->
                    execOperations.exec {
                        commandLine(
                            "curl", "-L",
                            "-X", "POST",
                            "-H", "Accept: application/vnd.github+json",
                            "-H", "Authorization: Bearer ${githubToken.get()}",
                            "-H", "X-GitHub-Api-Version: 2022-11-28",
                            "-H", "Content-Type: application/octet-stream",
                            "https://uploads.github.com/repos/VincentMasselis/TPMS-advanced/releases/$releaseId/assets?name=${file.name}",
                            "--data-binary", "@${file.absolutePath}"
                        )
                    }
                }
            }
    }
}