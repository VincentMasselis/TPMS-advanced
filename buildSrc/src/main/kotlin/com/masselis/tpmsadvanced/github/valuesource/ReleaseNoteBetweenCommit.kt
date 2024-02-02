package com.masselis.tpmsadvanced.github.valuesource

import com.masselis.tpmsadvanced.github.valuesource.ReleaseNoteBetweenCommit.Parameters
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class ReleaseNoteBetweenCommit : ValueSource<String, Parameters> {

    interface Parameters : ValueSourceParameters {
        val fromCommitSha: Property<String>
        val toCommitSha: Property<String>
    }

    @get:Inject
    abstract val execOperations: ExecOperations

    override fun obtain(): String = ByteArrayOutputStream()
        .also { stdout ->
            execOperations
                .exec {
                    commandLine(
                        "git",
                        "--no-pager", // Run "log" command in non interactive mode
                        "log",
                        "--oneline", // Compacts the output
                        "--no-decorate", // Hides branches
                        "--no-merges",
                        "${parameters.fromCommitSha.get()}..${parameters.toCommitSha.get()}", // Every commit between the current commit and the previous one
                        "--"
                    )
                    standardOutput = stdout
                }
        }
        .use { it.toString() }
        .trim()
}
