package com.masselis.tpmsadvanced.gitflow.valuesource

import com.masselis.tpmsadvanced.gitflow.valuesource.CommitCountBetweenBranch.Parameters
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class CommitCountBetweenBranch : ValueSource<Int, Parameters> {

    interface Parameters : ValueSourceParameters {
        val fromBranch: Property<String>
        val toBranch: Property<String>
    }

    @get:Inject
    protected abstract val execOperations: ExecOperations

    private val logger = Logging.getLogger(CommitCountBetweenBranch::class.java)

    override fun obtain(): Int? = (ByteArrayOutputStream() to ByteArrayOutputStream())
        .also { (stdout, errout) ->
            execOperations.exec {
                commandLine(
                    "git",
                    "rev-list",
                    "--count",
                    "${parameters.toBranch.get()} ^${parameters.fromBranch.get()}",
                )
                standardOutput = stdout
                errorOutput = errout
                isIgnoreExitValue = true
            }.also { execResult ->
                when (execResult.exitValue) {
                    -1 -> {}
                    128 -> return null
                    else -> {
                        errout.use { it.toString() }.also(logger::error)
                        execResult.rethrowFailure()
                    }
                }
            }
        }
        .first
        .use { it.toString() }
        .trimIndent()
        .toInt()
}