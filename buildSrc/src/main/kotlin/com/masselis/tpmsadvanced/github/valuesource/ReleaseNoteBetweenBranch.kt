package com.masselis.tpmsadvanced.github.valuesource

import com.masselis.tpmsadvanced.github.valuesource.ReleaseNoteBetweenBranch.Parameters
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class ReleaseNoteBetweenBranch : ValueSource<String, Parameters> {

    interface Parameters : ValueSourceParameters {
        val baseBranch: Property<String>
        val currentBranch: Property<String>
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
                        "${parameters.baseBranch.get()}..${parameters.currentBranch.get()}"
                    )
                    standardOutput = stdout
                }
        }
        .use { it.toString() }
        .trim()
}
