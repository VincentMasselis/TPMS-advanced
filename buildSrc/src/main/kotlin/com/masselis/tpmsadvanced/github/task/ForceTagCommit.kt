package com.masselis.tpmsadvanced.github.task

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

internal abstract class ForceTagCommit : DefaultTask() {

    @get:Inject
    abstract val execOperations: ExecOperations

    @get:Input
    abstract val tag: Property<String>

    init {
        group = "publishing"
        description = "Tags the commit"
    }

    @TaskAction
    internal fun process() {
        // Pull every tag from the remote
        execOperations.exec {
            commandLine(
                "git",
                "fetch",
                "--all",
                "--tags"
            )
        }

        // Add or move the tag to the current commit
        execOperations.exec {
            commandLine(
                "git",
                "tag",
                "-f",
                tag.get()
            )
        }

        // Push the tag to the remote
        execOperations.exec {
            commandLine(
                "git",
                "push",
                "-f",
                "--tags"
            )
        }
    }
}