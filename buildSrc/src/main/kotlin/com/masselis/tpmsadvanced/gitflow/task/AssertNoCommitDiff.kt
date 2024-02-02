package com.masselis.tpmsadvanced.gitflow.task

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class AssertNoCommitDiff : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:Input
    abstract val baseBranch: Property<String>

    @get:Input
    abstract val currentBranch: Property<String>

    init {
        group = "verification"
        description = "Check currentBranch is up-to-date with baseBranch"
    }

    @TaskAction
    internal fun process() {
        ByteArrayOutputStream()
            .also { stdout ->
                execOperations.exec {
                    commandLine(
                        "git",
                        "cherry",
                        "-v",
                        currentBranch.get(),
                        baseBranch.get()
                    )
                    standardOutput = stdout
                }
            }
            .use { it.toString() }
            .also { unmergedCommits ->
                if (unmergedCommits.isNotBlank())
                    throw GradleException("Some commits from \"${baseBranch.get()}\" are missing in \"${currentBranch.get()}\". Missing commits: $unmergedCommits")
            }
    }
}