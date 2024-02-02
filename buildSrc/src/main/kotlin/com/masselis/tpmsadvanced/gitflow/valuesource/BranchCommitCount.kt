package com.masselis.tpmsadvanced.gitflow.valuesource

import com.masselis.tpmsadvanced.gitflow.valuesource.BranchCommitCount.Parameters
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class BranchCommitCount : ValueSource<Int, Parameters> {

    interface Parameters : ValueSourceParameters {
        val baseBranch: Property<String>
        val currentBranch: Property<String>
    }

    @get:Inject
    abstract val execOperations: ExecOperations

    override fun obtain(): Int? = ByteArrayOutputStream()
        .also {
            execOperations.exec {
                isIgnoreExitValue = true
                commandLine(
                    "git",
                    "--no-pager", // Run "log" command in non interactive mode
                    "log",
                    "--oneline", // Compacts the output
                    "--no-decorate", // Hides branches
                    "--no-merges",
                    "${parameters.baseBranch.get()}..${parameters.currentBranch.get()}"
                )
                standardOutput = it
                errorOutput = ByteArrayOutputStream()
            }.also {
                // Returns null is the current branch is not "release/*"
                if (it.exitValue != 0)
                    return null
            }
        }
        .use { it.toString() }
        .trim()
        .split('\n')
        .count()
        .also(::println)
}