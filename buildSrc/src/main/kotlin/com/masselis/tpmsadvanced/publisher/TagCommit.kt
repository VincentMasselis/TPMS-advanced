package com.masselis.tpmsadvanced.publisher

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

internal abstract class TagCommit : DefaultTask() {

    @get:Input
    public abstract val tag: Property<String>

    @get:Inject
    internal abstract val execOperations: ExecOperations

    init {
        group = "publishing"
        description = "Tags the commit"
    }

    @TaskAction
    internal fun process() {
        // Pushes the git tag
        execOperations.exec {
            commandLine("git", "fetch")
            args("--all", "--tags")
        }.assertNormalExitValue()

        // If the tag already exists, this step fails
        execOperations.exec {
            commandLine("git", "tag", tag.get())
        }.assertNormalExitValue()

        // Push the tag to the remote
        execOperations.exec {
            commandLine("git", "push")
            args("--tags")
        }.assertNormalExitValue()
    }
}