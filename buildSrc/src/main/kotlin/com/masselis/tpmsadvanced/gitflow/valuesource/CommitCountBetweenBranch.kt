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

    override fun obtain(): Int? {
        val stdout = ByteArrayOutputStream()
        val stderr = ByteArrayOutputStream()
        execOperations.exec {
            commandLine("git", "rev-list", "--count", parameters.toBranch.get(), "^${parameters.fromBranch.get()}")
            standardOutput = stdout
            errorOutput = stderr
            isIgnoreExitValue = true
        }.also { result ->
            stderr.use(ByteArrayOutputStream::toString).also(logger::warn)
            result.rethrowFailure()
        }
        return stdout.use(ByteArrayOutputStream::toString).trim().toIntOrNull()
    }
}
