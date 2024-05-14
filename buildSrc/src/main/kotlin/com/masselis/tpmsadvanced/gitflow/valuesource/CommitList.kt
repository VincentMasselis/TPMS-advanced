package com.masselis.tpmsadvanced.gitflow.valuesource

import com.masselis.tpmsadvanced.gitflow.valuesource.CommitList.Parameters
import org.gradle.api.GradleException
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@Suppress("NAME_SHADOWING")
internal abstract class CommitList : ValueSource<List<String>, Parameters> {

    interface Parameters : ValueSourceParameters {
        val fromBranch: Property<String>
        val toBranch: Property<String>
    }

    private val logger = Logging.getLogger(CommitList::class.java)

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun obtain(): List<String> = ByteArrayOutputStream()
        .use { stdout ->
            val errOut = ByteArrayOutputStream()
            execOperations.exec {
                isIgnoreExitValue = true
                commandLine(
                    "git",
                    "cherry",
                    "-v",
                    parameters.toBranch.get(),
                    parameters.fromBranch.get(),
                )
                errorOutput = errOut
                standardOutput = stdout
            }.also { execResult ->
                val errOut = errOut.use { it.toString() }
                when {
                    errOut.startsWith("fatal: unknown commit") -> throw GradleException(
                        "Unable to find \"${
                            errOut.substringAfter(
                                "fatal: unknown commit "
                            ).trimIndent()
                        }\", is it available into your local git instance ?"
                    )

                    else -> {
                        logger.error(errOut)
                        execResult.rethrowFailure()
                    }
                }
            }
            stdout.toString()
        }
        .trimIndent()
        .split('\n')
        .filter { it.isNotBlank() }
}