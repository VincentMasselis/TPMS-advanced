package com.masselis.tpmsadvanced.gitflow.task

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

internal abstract class AssertGitDiffIsEmpty : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    init {
        group = "gitflow"
        description = "Asserts the current working directory is empty"
    }

    @TaskAction
    internal fun process() = execOperations
        .exec {
            isIgnoreExitValue = true
            commandLine("git", "diff", "--exit-code")
        }.also { result ->
            if(result.exitValue != 0)
                throw GradleException("Cannot continue, your git working directory must be empty")
        }
}