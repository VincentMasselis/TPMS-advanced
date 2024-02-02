package com.masselis.tpmsadvanced.github.task

import com.masselis.tpmsadvanced.github.valuesource.ReleaseNoteBetweenBranch
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
import javax.inject.Inject

internal abstract class CreateBetaRelease : DefaultTask() {

    @get:Inject
    abstract val providerFactory: ProviderFactory

    @get:Inject
    abstract val execOperations: ExecOperations

    @get:Input
    abstract val githubToken: Property<String>

    @get:Input
    abstract val tagName: Property<String>

    @get:Input
    abstract val betaBranch: Property<String>

    @get:Input
    abstract val prodBranch: Property<String>

    private val releaseNote
        get() = providerFactory.from(ReleaseNoteBetweenBranch::class) {
            baseBranch = prodBranch
            currentBranch = betaBranch
        }

    init {
        group = "publishing"
        description = "Creates a beta release on github"
    }

    @TaskAction
    internal fun process() {
        val releaseNote = releaseNote.get()
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
                        "body" to JsonPrimitive(releaseNote),
                        "prerelease" to JsonPrimitive(true)
                    )
                )
            )
        }
    }
}