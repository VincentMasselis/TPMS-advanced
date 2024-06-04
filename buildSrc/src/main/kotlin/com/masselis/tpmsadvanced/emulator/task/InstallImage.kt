package com.masselis.tpmsadvanced.emulator.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

internal abstract class InstallImage : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:InputDirectory
    abstract val sdkPath: DirectoryProperty

    @get:Input
    abstract val emulatorPackage: Property<String>

    init {
        group = "android"
        description = "Download and install Emulator image"
    }

    @TaskAction
    internal fun process() {
        execOperations.exec {
            commandLine(
                "${sdkPath.get().asFile}/cmdline-tools/latest/bin/sdkmanager",
                "--install", emulatorPackage.get(),
            )
        }
    }
}