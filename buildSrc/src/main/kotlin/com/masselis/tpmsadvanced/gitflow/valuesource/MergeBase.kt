package com.masselis.tpmsadvanced.gitflow.valuesource

import com.masselis.tpmsadvanced.gitflow.valuesource.MergeBase.Parameters
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/**
 * The best common ancestor of [Parameters.a] and [Parameters.b], or an empty string if the two
 * revisions share no history at all.
 */
internal abstract class MergeBase : ValueSource<String, Parameters> {

    internal interface Parameters : ValueSourceParameters {
        val a: Property<String>
        val b: Property<String>
    }

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun obtain(): String = ByteArrayOutputStream()
        .also { stdout ->
            execOperations.exec {
                isIgnoreExitValue = true
                commandLine("git", "merge-base", parameters.a.get(), parameters.b.get())
                standardOutput = stdout
            }.also { result ->
                // Exit code 1 with empty stdout means "no common ancestor" - not an error for our purposes.
                when (result.exitValue) {
                    0, 1 -> Unit
                    else -> result.rethrowFailure()
                }
            }
        }
        .use { it.toString() }
        .trim()
}
