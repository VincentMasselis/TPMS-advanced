package com.masselis.tpmsadvanced.github

import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class CommitShaValueSource :
    ValueSource<String, CommitShaValueSource.Parameters> {

    interface Parameters : ValueSourceParameters {
        val backwardCount: Property<Int>
    }

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun obtain(): String = ByteArrayOutputStream()
        .also {
            execOperations.exec {
                commandLine(
                    "git",
                    "rev-parse",
                    "HEAD^${parameters.backwardCount.get()}"
                )
                standardOutput = it
            }
        }
        .use { it.toString() }
        .trim()
}