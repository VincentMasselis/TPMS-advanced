package com.masselis.tpmsadvanced.gitflow.valuesource

import com.masselis.tpmsadvanced.gitflow.valuesource.GitTagList.Parameters
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class GitTagList : ValueSource<List<String>, Parameters> {

    interface Parameters : ValueSourceParameters {
        val inputFilter: Property<String>
    }

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun obtain(): List<String> = ByteArrayOutputStream()
        .also {
            execOperations.exec {
                commandLine(
                    "git",
                    "tag",
                    "-l",
                    parameters.inputFilter.get(),
                )
                standardOutput = it
            }
        }
        .use { it.toString() }
        .trimIndent()
        .split('\n')
        .filter { it.isNotBlank() }
}