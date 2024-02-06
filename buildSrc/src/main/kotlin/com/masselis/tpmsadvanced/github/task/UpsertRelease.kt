package com.masselis.tpmsadvanced.github.task

import com.masselis.tpmsadvanced.github.valuesource.BackwardCommitSha
import com.masselis.tpmsadvanced.github.valuesource.ListRelease
import com.masselis.tpmsadvanced.github.valuesource.ReleaseNoteBetweenBranch
import com.masselis.tpmsadvanced.github.valuesource.ReleaseNoteBetweenCommit
import org.gradle.kotlin.dsl.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.process.ExecOperations
import javax.inject.Inject

internal abstract class UpsertRelease : DefaultTask() {

    @get:Inject
    abstract val providerFactory: ProviderFactory

    @get:Inject
    abstract val execOperations: ExecOperations

    @get:Input
    abstract val githubToken: Property<String>

    @get:Input
    abstract val tagName: Property<String>

    @get:Input
    abstract val preRelease: Property<Boolean>

    @get:Input
    abstract val preReleaseBranch: Property<String>

    @get:Input
    abstract val releaseBranch: Property<String>

    private val releaseList
        get() = providerFactory.from(ListRelease::class)

    private val releaseNotes
        get() = preRelease.flatMap { isPreRelease ->
            if (isPreRelease)
                providerFactory.from(ReleaseNoteBetweenBranch::class) {
                    baseBranch = releaseBranch
                    currentBranch = preReleaseBranch
                }
            else
                providerFactory.from(ReleaseNoteBetweenCommit::class) {
                    fromCommitSha = providerFactory.from(BackwardCommitSha::class) {
                        backwardCount = 1
                    }
                    toCommitSha = providerFactory.from(BackwardCommitSha::class) {
                        backwardCount = 0
                    }
                }
        }

    init {
        group = "publishing"
        description = "Creates or updates a release on github"
    }

    @TaskAction
    internal fun process(): Any = releaseList
        .get()
        .map { it["id"]!!.jsonPrimitive.int to it["tag_name"]!!.jsonPrimitive.content }
        .firstOrNull { (_, tag) -> tag == tagName.get() }
        ?.let { (releaseId) ->
            // A release already exists
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
                            "body" to JsonPrimitive(releaseNotes.get()),
                            "prerelease" to JsonPrimitive(preRelease.get())
                        )
                    ).toString()
                )
            }
        }
        ?: run {
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
}