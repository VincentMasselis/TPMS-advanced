package com.masselis.tpmsadvanced.emulator.task

import com.masselis.tpmsadvanced.emulator.valuesource.RunningEmulators
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
import java.time.Duration
import javax.inject.Inject

internal abstract class WaitForEmulator : DefaultTask() {

    @get:Inject
    protected abstract val providerFactory: ProviderFactory

    @get:Inject
    abstract val execOperations: ExecOperations

    @get:InputDirectory
    abstract val sdkPath: DirectoryProperty

    @get:Input
    abstract val emulatorName: Property<String>

    init {
        group = "android"
        description = "Wait for an android device to run"
        timeout = Duration.ofMinutes(10)
    }

    @TaskAction
    internal fun process() {
        val emulatorId = providerFactory
            .from(RunningEmulators::class) { this.sdkPath = this@WaitForEmulator.sdkPath }
            .get()
            .single { (_, name) -> name == emulatorName.get() }
            .id
        execOperations.exec {
            commandLine(
                "${sdkPath.get()}/platform-tools/adb",
                "-s", emulatorId,
                "wait-for-device",
                "shell",
                "while [[ -z \$(getprop sys.boot_completed | tr -d '\\r') ]]; do sleep 1; done; input keyevent 82"
            )
        }
    }
}
