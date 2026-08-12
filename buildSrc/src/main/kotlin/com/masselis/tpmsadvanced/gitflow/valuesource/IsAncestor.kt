package com.masselis.tpmsadvanced.gitflow.valuesource

import com.masselis.tpmsadvanced.gitflow.valuesource.IsAncestor.Parameters
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * Whether [Parameters.ancestor] is reachable from [Parameters.descendant] (including being the
 * same commit). Backed by `git merge-base --is-ancestor`, whose exit code alone answers the
 * question, no stdout parsing needed.
 */
internal abstract class IsAncestor : ValueSource<Boolean, Parameters> {

    internal interface Parameters : ValueSourceParameters {
        val ancestor: Property<String>
        val descendant: Property<String>
    }

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun obtain(): Boolean = execOperations
        .exec {
            isIgnoreExitValue = true
            commandLine(
                "git",
                "merge-base",
                "--is-ancestor",
                parameters.ancestor.get(),
                parameters.descendant.get()
            )
        }
        .exitValue
        .let { exitValue ->
            when (exitValue) {
                0 -> true
                1 -> false
                else -> throw GradleException(
                    "git merge-base --is-ancestor failed (exit $exitValue) for " +
                            "\"${parameters.ancestor.get()}\" / \"${parameters.descendant.get()}\" " +
                            "- do both revisions exist locally? A fetch may be missing."
                )
            }
        }
}
