package com.masselis.tpmsadvanced.gitflow.valuesource

import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class CurrentBranch : ValueSource<String, ValueSourceParameters.None> {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun obtain(): String = ByteArrayOutputStream()
        .also {
            execOperations.exec {
                commandLine(
                    "git",
                    "rev-parse",
                    "--abbrev-ref",
                    "--symbolic-full-name",
                    "@{u}"
                )
                standardOutput = it
            }
        }
        .use { it.toString() }
        .trimIndent()
}
