package com.masselis.tpmsadvanced.gitflow.valuesource

import com.masselis.tpmsadvanced.gitflow.valuesource.CommitList.Parameters
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class CommitList : ValueSource<List<String>, Parameters> {

    interface Parameters : ValueSourceParameters {
        val fromBranch: Property<String>
        val toBranch: Property<String>
    }

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun obtain(): List<String> = ByteArrayOutputStream()
        .also { stdout ->
            execOperations.exec {
                commandLine(
                    "git",
                    "cherry",
                    "-v",
                    parameters.fromBranch.get(),
                    parameters.toBranch.get(),
                )
                standardOutput = stdout
            }
        }
        .use { it.toString() }
        .trimIndent()
        .split('\n')
        .filter { it.isNotBlank() }
}