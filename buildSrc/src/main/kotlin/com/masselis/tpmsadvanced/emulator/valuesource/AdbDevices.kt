package com.masselis.tpmsadvanced.emulator.valuesource

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class AdbDevices : ValueSource<List<String>, AdbDevices.Parameters> {

    interface Parameters : ValueSourceParameters {
        val sdkPath: DirectoryProperty
    }

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun obtain(): List<String> = ByteArrayOutputStream()
        .use { stdout ->
            val errOut = ByteArrayOutputStream()
            execOperations.exec {
                isIgnoreExitValue = true
                commandLine(
                    "${parameters.sdkPath.get().asFile}/platform-tools/adb",
                    "emu", "avd", "name"
                )
                standardOutput = stdout
                errorOutput = errOut
            }
            errOut.close()
            stdout.toString()
        }
        .trimIndent()
        .split('\n')
        .filter { it.isNotBlank() }
        .dropLastWhile { it == "OK" }
}