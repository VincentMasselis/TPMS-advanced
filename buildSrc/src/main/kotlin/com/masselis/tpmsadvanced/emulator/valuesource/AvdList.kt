package com.masselis.tpmsadvanced.emulator.valuesource

import com.masselis.tpmsadvanced.emulator.valuesource.AvdList.Parameters
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class AvdList : ValueSource<List<String>, Parameters> {

    interface Parameters : ValueSourceParameters {
        val sdkPath: DirectoryProperty
    }

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun obtain(): List<String> = ByteArrayOutputStream()
        .also { stdout ->
            execOperations.exec {
                commandLine(
                    "${parameters.sdkPath.get().asFile}/emulator/emulator",
                    "-list-avds",
                )
                standardOutput = stdout
            }
        }
        .use { it.toString() }
        .trimIndent()
        .split('\n')
        .filter { it.isNotBlank() }
}