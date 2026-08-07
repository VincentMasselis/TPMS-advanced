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
        execOperations.exec { commandLine("git", "fetch", "--all", "--tags") }
        // Fails immediately if the tag already exists - the actual duplicate-release guard.
        execOperations.exec { commandLine("git", "tag", tag.get().toString()) }
        execOperations.exec { commandLine("git", "push", "--tags") }
    }
}
