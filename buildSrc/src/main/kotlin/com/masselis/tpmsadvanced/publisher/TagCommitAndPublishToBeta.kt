package com.masselis.tpmsadvanced.publisher

import com.google.api.client.http.FileContent
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.of
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
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

    @get:Inject
    internal abstract val providerFactory: ProviderFactory

    init {
        group = "publishing"
        description = "Tag the commit and push the bundle to the filled play store track"
    }

    @TaskAction
    internal fun process() {
        // Pushes the git tag
        execOperations.exec {
            commandLine("git", "fetch")
            args("--all", "--tags")
        }.assertNormalExitValue()
        execOperations.exec {
            // If the tag already exists, this step fails
            commandLine("git", "tag", "vc${currentVc.get()}")
        }.assertNormalExitValue()
        execOperations.exec {
            commandLine("git", "push")
            args("--tags")
        }.assertNormalExitValue()

        // Creates a github release
        ByteArrayOutputStream()
            .also { stdout ->
                val fromCommit = providerFactory.of(CommitShaValueSource::class) {
                    parameters { backwardCount.set(1) }
                }
                val toCommit = providerFactory.of(CommitShaValueSource::class) {
                    parameters { backwardCount.set(0) }
                }
                execOperations.exec {
                    commandLine(
                        "git",
                        "--no-pager", // Run "log" command in non interractive mode
                        "log",
                        "--oneline", // Compacts the output
                        "--no-decorate", // Hides branches
                        "--no-merges",
                        "${fromCommit.get()}..${toCommit.get()}", // Every commit between the current commit and the previous one
                        "--"
                    )
                    standardOutput = stdout
                }.assertNormalExitValue()
            }
            .toString()
            .trim()
            .also { commitsNames ->
                execOperations.exec {
                    commandLine(
                        "curl", "-L",
                        "-X", "POST",
                        "-H", "Accept: application/vnd.github+json",
                        "-H", "Authorization: Bearer ${System.getenv("GITHUB_TOKEN")}",
                        "-H", "X-GitHub-Api-Version: 2022-11-28",
                        "https://api.github.com/repos/VincentMasselis/TPMS-advanced/releases",
                        "-d",
                        JsonObject(
                            mapOf(
                                "tag_name" to JsonPrimitive("vc${currentVc.get()}"),
                                "body" to JsonPrimitive(commitsNames),
                                "prerelease" to JsonPrimitive(true)
                            )
                        )
                    )
                }.assertNormalExitValue()
            }

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
                                    .setText(this@TagCommitAndPublishToBeta.releaseNotes.get().asFile.readText())
                                versionCodes.set(0, versionCode)
                            }
                        }
                    }
            }
    }
}
