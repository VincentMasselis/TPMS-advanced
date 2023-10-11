package com.masselis.tpmsadvanced.publisher

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class PromoteGithubRelease : DefaultTask() {

    private val githubToken = project.extensions.getByType<AndroidPublisherExtension>().githubToken

    @get:Input
    public abstract val tagName: Property<String>

    @get:Inject
    internal abstract val execOperations: ExecOperations

    init {
        group = "publishing"
        description = "Removes the tag \"pre-release\" for a release in github"
    }

    @TaskAction
    internal fun process() {
        ByteArrayOutputStream()
            .also { stdout ->
                execOperations.exec {
                    commandLine(
                        "curl", "-L",
                        "-H", "Accept: application/vnd.github+json",
                        "-H", "Authorization: Bearer ${githubToken.get()}",
                        "-H", "X-GitHub-Api-Version: 2022-11-28",
                        "https://api.github.com/repos/VincentMasselis/TPMS-advanced/releases/tags/${tagName.get()}"
                    )
                    standardOutput = stdout
                }.assertNormalExitValue()
            }
            .toString()
            .let(Json::parseToJsonElement)
            .jsonObject
            .getValue("id")
            .jsonPrimitive
            .int
            .let { releaseId ->
                execOperations.exec {
                    commandLine(
                        "curl", "-L",
                        "-X", "PATCH",
                        "-H", "Accept: application/vnd.github+json",
                        "-H", "Authorization: Bearer ${githubToken.get()}",
                        "-H", "X-GitHub-Api-Version: 2022-11-28",
                        "https://api.github.com/repos/VincentMasselis/TPMS-advanced/releases/$releaseId",
                        "-d",
                        JsonObject(
                            mapOf(
                                "prerelease" to JsonPrimitive(false)
                            )
                        ).toString()
                    )
                }.assertNormalExitValue()
            }
    }
}