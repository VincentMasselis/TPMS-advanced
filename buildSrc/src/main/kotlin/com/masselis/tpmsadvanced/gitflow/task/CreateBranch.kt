package com.masselis.tpmsadvanced.gitflow.task

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

internal abstract class CreateBranch : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:Input
    abstract val branch: Property<String>

    init {
        description = "Creates a new branch and switch to this branch automatically"
    }

    @TaskAction
    internal fun process() {
        execOperations.exec {
            commandLine(
                "git",
                "checkout",
                "-b",
                branch.get(),
            )
        }
    }
}