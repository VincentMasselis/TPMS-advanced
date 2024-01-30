package com.masselis.tpmsadvanced.gitflow

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@Suppress("LeakingThis")
internal abstract class AssertParent : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:Input
    abstract val currentBranch: Property<String>

    @get:Input
    abstract val parentBranch: Property<String>

    init {
        group = "verification"
        description = "Check the current branch's source is \"parentBranch\""
    }

    @TaskAction
    internal fun process() {
        ByteArrayOutputStream()
            .also { stdout ->
                execOperations.exec {
                    commandLine(
                        "git",
                        "branch",
                        "--contains",
                        parentBranch.get()
                    )
                    standardOutput = stdout
                }
            }
            .use { it.toString() }
            .trimIndent()
            .split('\n')
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .singleOrNull { it == parentBranch.get() }
            .also { parent ->
                if (parent == null)
                    throw GradleException("Parent branch \"${parentBranch.get()}\" not found for the current branch \"${currentBranch.get()}\"")
            }
    }
}