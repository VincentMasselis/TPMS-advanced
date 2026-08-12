package com.masselis.tpmsadvanced.gitflow.valuesource

import com.masselis.tpmsadvanced.gitflow.model.WorkingTreeState
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/** Reports the working tree split into the same buckets `git status` uses. */
internal abstract class WorkingTree : ValueSource<WorkingTreeState, ValueSourceParameters.None> {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun obtain(): WorkingTreeState = WorkingTreeState(
        staged = names("diff", "--cached", "--name-only"),
        unstaged = names("diff", "--name-only"),
        untracked = names("ls-files", "--others", "--exclude-standard"),
    )

    private fun names(vararg gitArgs: String): Set<String> {
        val stdout = ByteArrayOutputStream()
        execOperations.exec {
            commandLine(listOf("git") + gitArgs)
            standardOutput = stdout
        }
        return stdout.use { it.toString() }
            .lineSequence()
            .map(String::trim)
            .filter(String::isNotBlank)
            .toSet()
    }
}
