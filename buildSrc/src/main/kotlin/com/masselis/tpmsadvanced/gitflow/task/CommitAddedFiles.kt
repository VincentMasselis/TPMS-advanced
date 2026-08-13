package com.masselis.tpmsadvanced.gitflow.task

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject


internal abstract class CommitAddedFiles : DefaultTask() {

    @get:Input
    abstract val commitMessage: Property<String>

    @get:Inject
    protected abstract val execOperations: ExecOperations

    init {
        group = "gitflow"
        description = "Commit added files"
    }

    @TaskAction
    internal fun process() = commitMessage
        .orNull
        ?.ifBlank { null }
        .let { it ?: error("Commit message is missing") }
        .also { execOperations.exec { commandLine("git", "commit", "-m", it) } }
}
