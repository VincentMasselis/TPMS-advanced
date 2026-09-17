package com.masselis.tpmsadvanced.gitflow.valuesource

import com.masselis.tpmsadvanced.gitflow.valuesource.GitBranchList.Parameters
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class GitBranchList : ValueSource<List<String>, Parameters> {

    interface Parameters : ValueSourceParameters {
        val inputFilter: Property<String>
    }

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun obtain(): List<String> = ByteArrayOutputStream()
        .also {
            execOperations.exec {
                commandLine("git", "branch", "-r", "--list", parameters.inputFilter.get())
                standardOutput = it
            }
        }
        .use { it.toString() }
        .lineSequence()
        // A leading "* " marks whichever remote-tracking ref the local HEAD happens to point at.
        .map { it.removePrefix("*").trim() }
        .filter { it.isNotBlank() }
        // "origin/HEAD -> origin/develop" is a symbolic ref, not a real branch - `git rev-list`
        // and friends choke on the "-> " part if it's left in.
        .filterNot { "->" in it }
        .toList()
}
