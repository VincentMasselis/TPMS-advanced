package com.masselis.tpmsadvanced.gitflow.task

import SemanticVersion
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

internal abstract class TagCommit : DefaultTask() {

    @get:Input
    abstract val tag: Property<SemanticVersion>

    @get:Inject
    abstract val execOperations: ExecOperations

    init {
        group = "publishing"
        description = "Tags the commit"
    }

    @TaskAction
    internal fun process() {
        // Fetch every tags
        execOperations.exec {
            commandLine("git", "fetch")
            args("--all", "--tags")
        }

        // If the tag already exists, this step fails
        execOperations.exec {
            commandLine("git", "tag", tag.get())
        }

        // Push the tag to the remote
        execOperations.exec {
            commandLine("git", "push")
            args("--tags")
        }
    }
}