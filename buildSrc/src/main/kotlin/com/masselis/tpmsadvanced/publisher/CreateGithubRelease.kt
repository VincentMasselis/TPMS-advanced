package com.masselis.tpmsadvanced.publisher

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.of
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class CreateGithubRelease : DefaultTask() {

    private val githubToken = project.extensions.getByType<AndroidPublisherExtension>().githubToken

    @get:Input
    public abstract val tagName: Property<String>

    @get:Inject
    internal abstract val providerFactory: ProviderFactory

    @get:Inject
    internal abstract val execOperations: ExecOperations

    init {
        group = "publishing"
        description = "Creates a release on github"
    }

    @TaskAction
    internal fun process() {
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
                        "-H", "Authorization: Bearer ${githubToken.get()}",
                        "-H", "X-GitHub-Api-Version: 2022-11-28",
                        "https://api.github.com/repos/VincentMasselis/TPMS-advanced/releases",
                        "-d",
                        JsonObject(
                            mapOf(
                                "tag_name" to JsonPrimitive(tagName.get()),
                                "body" to JsonPrimitive(commitsNames),
                                "prerelease" to JsonPrimitive(true)
                            )
                        )
                    )
                }.assertNormalExitValue()
            }
    }
}