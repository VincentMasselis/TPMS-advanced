package com.masselis.tpmsadvanced.emulator.task

import com.masselis.tpmsadvanced.emulator.valuesource.AvdList
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.from
import org.gradle.process.ExecOperations
import javax.inject.Inject

internal abstract class CreateEmulator : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:Inject
    protected abstract val providerFactory: ProviderFactory

    @get:InputDirectory
    abstract val sdkPath: DirectoryProperty

    @get:Input
    abstract val emulatorPackage: Property<String>

    @get:Input
    abstract val emulatorName: Property<String>

    init {
        group = "android"
        description = "Create the emulator"
    }

    @TaskAction
    internal fun process() {
        providerFactory.from(AvdList::class) { sdkPath = this@CreateEmulator.sdkPath }
            .get()
            .contains(emulatorName.get())
            .also { alreadyInstalled ->
                if (alreadyInstalled)
                    return
            }
        execOperations.exec {
            commandLine(
                "${sdkPath.get().asFile}/cmdline-tools/latest/bin/avdmanager",
                "create", "avd",
                "-n", emulatorName.get(),
                // Points to id: 19 or "pixel_2_xl", run `avdmanager list device` to discover more definitions
                "-d", "19",
                "--package", emulatorPackage.get()
            )
        }
    }
}