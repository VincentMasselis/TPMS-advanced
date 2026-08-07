package com.masselis.tpmsadvanced.gitflow.valuesource

import com.masselis.tpmsadvanced.gitflow.model.HeadState
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/**
 * Resolves HEAD's own local branch, purely locally, with no dependency on an upstream being
 * configured. Falls back to [HeadState.Detached] when HEAD isn't on a branch.
 */
internal abstract class CurrentBranch : ValueSource<HeadState, ValueSourceParameters.None> {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun obtain(): HeadState {
        val symbolicRef = ByteArrayOutputStream()
        execOperations.exec {
            isIgnoreExitValue = true
            commandLine("git", "symbolic-ref", "--quiet", "--short", "HEAD")
            standardOutput = symbolicRef
        }.also { execResult ->
            if (execResult.exitValue == 0)
                return HeadState.OnBranch(symbolicRef.use { it.toString() }.trim())
        }
        return ByteArrayOutputStream()
            .also { sha ->
                execOperations.exec {
                    commandLine("git", "rev-parse", "HEAD")
                    standardOutput = sha
                }
            }
            .use { it.toString() }
            .trim()
            .let(HeadState::Detached)
    }
}
