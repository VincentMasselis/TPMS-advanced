package com.masselis.tpmsadvanced.publisher

import com.google.api.client.http.FileContent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getValue
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

public abstract class PromoteToMain : DefaultTask(), ServiceHolder {

    @get:Input
    public abstract val packageName: Property<String>

    @get:Input
    public abstract val currentVc: Property<Int>

    @get:InputDirectory
    public abstract val screenshotDirectory: DirectoryProperty

    @get:Inject
    internal abstract val execOperations: ExecOperations

    init {
        group = "publishing"
    }

    @TaskAction
    internal fun process() {
        // Updates the current github release
        ByteArrayOutputStream()
            .also { stdout ->
                execOperations.exec {
                    commandLine(
                        "curl", "-L",
                        "-H", "Accept: application/vnd.github+json",
                        "-H", "Authorization: Bearer ${System.getenv("GITHUB_TOKEN")}",
                        "-H", "X-GitHub-Api-Version: 2022-11-28",
                        "https://api.github.com/repos/VincentMasselis/TPMS-advanced/releases/tags/vc${currentVc.get()}"
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
                        "-H", "Authorization: Bearer ${System.getenv("GITHUB_TOKEN")}",
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

        // Change the track for the play store relase and push screenshots to listings
        val packageName by packageName
        androidPublisher
            .edits()
            .withEdit(this, packageName) { edit ->
                tracks()
                    .get(packageName, edit.id, "beta")
                    .execute()
                    .releases
                    .first()
                    .also { betaTrack ->
                        // Check if the artifact to promote is equals to the current version code
                        betaTrack.versionCodes
                            .first()
                            .toInt()
                            .also { playStoreVc ->
                                if (playStoreVc != currentVc.get())
                                    throw GradleException("Current commit version code (${currentVc.get()}) differs to the current in beta from the play store ($playStoreVc)")
                            }
                    }
                    .also { betaTrack ->
                        // Take the atifact and release note from beta and push them in production
                        updateTrack(packageName, edit.id, "production") {
                            releases.first().apply {
                                versionCodes.set(0, betaTrack.versionCodes.first())
                                setReleaseNotes(betaTrack.releaseNotes)
                            }
                        }
                    }
                if (screenshotDirectory.asFileTree.isEmpty.not()) {
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
}
