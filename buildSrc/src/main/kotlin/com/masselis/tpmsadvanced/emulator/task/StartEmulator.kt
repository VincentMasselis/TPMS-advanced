package com.masselis.tpmsadvanced.emulator.task

import com.masselis.tpmsadvanced.emulator.valuesource.RunningEmulators
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.from
import org.jetbrains.kotlin.gradle.utils.provider
import java.lang.Thread.sleep
import java.time.Duration
import javax.inject.Inject

internal abstract class StartEmulator : DefaultTask() {

    @get:Inject
    protected abstract val providerFactory: ProviderFactory

    @get:InputDirectory
    abstract val sdkPath: DirectoryProperty

    @get:Input
    abstract val emulatorName: Property<String>

    @get:InputFile
    abstract val standardOutput: RegularFileProperty

    @get:InputFile
    abstract val errorOutput: RegularFileProperty

    init {
        group = "android"
        description = "Starts the emulator"
        timeout = Duration.ofMinutes(2)
    }

    // Unlike other tasks which works with "ExecOperations", this one uses a "ProcessBuilder"
    // because is easier to launch a headless process with ProcessBuilder than ExecOperations.
    @TaskAction
    internal fun process() {
        if (isEmulatorRunning())
            return

        ProcessBuilder(
            "${sdkPath.get().asFile}/emulator/emulator",
            "-avd", emulatorName.get(),
            "-gpu", "swiftshader_indirect",
            "-camera-back", "none",
            "-no-window", "-no-snapshot", "-no-audio", "-no-boot-anim",
            "-qemu", "-m", "2048",
        ).redirectOutput(standardOutput.get().asFile)
            .redirectError(errorOutput.get().asFile)
            .start()

        while (true) {
            if (isEmulatorRunning()) break
            else sleep(50)
        }
    }

    private fun isEmulatorRunning() = providerFactory
        .from(RunningEmulators::class) { this.sdkPath = this@StartEmulator.sdkPath }
        .get()
        .any { (_, name) -> name == emulatorName.get() }
}