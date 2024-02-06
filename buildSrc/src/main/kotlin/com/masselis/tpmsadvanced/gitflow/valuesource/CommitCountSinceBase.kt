package com.masselis.tpmsadvanced.gitflow.valuesource

import com.masselis.tpmsadvanced.gitflow.valuesource.CommitCountSinceBase.Parameters
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class CommitCountSinceBase : ValueSource<Int, Parameters> {

    interface Parameters : ValueSourceParameters {
        val baseBranch: Property<String>
    }

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun obtain(): Int? = ByteArrayOutputStream()
        .also {
            execOperations.exec {
                commandLine(
                    "git",
                    "rev-list",
                    "--count",
                    "HEAD",
                    "^${parameters.baseBranch.get()}"
                )
                standardOutput = it
            }
        }
        .use { it.toString() }
        .trim()
        .toInt()
}