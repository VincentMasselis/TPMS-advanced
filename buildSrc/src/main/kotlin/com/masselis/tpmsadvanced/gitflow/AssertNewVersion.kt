package com.masselis.tpmsadvanced.gitflow

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class AssertNewVersion : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:Input
    abstract val version: Property<String>

    init {
        group = "verification"
        description = "Check the version to come was not created yet"
    }

    @TaskAction
    internal fun process() {
        ByteArrayOutputStream()
            .also {
                execOperations.exec {
                    commandLine(
                        "git",
                        "tag",
                        "-l",
                        version.get()
                    )
                    standardOutput = it
                }
            }
            .use { it.toString() }
            .trimIndent()
            .also {
                if (it.isNotBlank())
                    throw GradleException("Cannot create a new version for \"${version.get()}\", it already exists")
            }
    }
}
