package com.masselis.tpmsadvanced.github

import org.gradle.kotlin.dsl.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class CreateGithubRelease : DefaultTask() {

    @get:Input
    abstract val githubToken: Property<String>

    @get:Input
    abstract val tagName: Property<String>

    @get:Inject
    abstract val providerFactory: ProviderFactory

    @get:Inject
    abstract val execOperations: ExecOperations

    private val fromCommit
        get() = providerFactory.from(CommitShaValueSource::class) { backwardCount = 1 }

    private val toCommit
        get() = providerFactory.from(CommitShaValueSource::class) { backwardCount = 0 }

    init {
        group = "publishing"
        description = "Creates a release on github"
    }

    @TaskAction
    internal fun process() {
        ByteArrayOutputStream()
            .also { stdout ->
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
            .use { it.toString() }
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