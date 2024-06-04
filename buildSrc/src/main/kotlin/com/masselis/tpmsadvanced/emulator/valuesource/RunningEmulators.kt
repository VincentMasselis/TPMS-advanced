package com.masselis.tpmsadvanced.emulator.valuesource

import com.masselis.tpmsadvanced.emulator.valuesource.RunningEmulators.Instance
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class RunningEmulators :
    ValueSource<List<Instance>, RunningEmulators.Parameters> {

    interface Parameters : ValueSourceParameters {
        val sdkPath: DirectoryProperty
    }

    data class Instance(val id: String, val name: String)

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun obtain(): List<Instance> = ByteArrayOutputStream()
        .use { stdout ->
            execOperations.exec {
                commandLine(
                    "${parameters.sdkPath.get().asFile}/platform-tools/adb",
                    "devices"
                )
                standardOutput = stdout
            }
            stdout.toString()
        }
        .trimIndent()
        .split('\n')
        .filter { it.startsWith("emulator-") }
        .map { it.split('\t').first() }
        .map { id ->
            ByteArrayOutputStream()
                .use { stdout ->
                    execOperations.exec {
                        commandLine(
                            "${parameters.sdkPath.get().asFile}/platform-tools/adb",
                            "-s", id,
                            "emu", "avd", "name"
                        )
                        standardOutput = stdout
                    }
                    stdout.toString()
                }
                .trimIndent()
                .split('\n')
                .first()
                .let { Instance(id, it) }
        }
}
