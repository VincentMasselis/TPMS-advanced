package com.masselis.tpmsadvanced.emulator.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

internal abstract class AcceptLicense : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:InputDirectory
    abstract val sdkPath: DirectoryProperty

    init {
        group = "android"
        description = "Accept SDK Manage license"
    }

    @TaskAction
    internal fun process() {
        execOperations.exec {
            commandLine(
                "${sdkPath.get().asFile}/cmdline-tools/latest/bin/sdkmanager",
                "--licenses" // Automatically accepts licences
            )
        }
    }
}