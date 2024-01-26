package com.masselis.tpmsadvanced.gitflow

import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

public abstract class CurrentBranchValueSource : ValueSource<String, ValueSourceParameters.None> {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun obtain(): String {
        val stdout = ByteArrayOutputStream()
        execOperations.exec {
            commandLine(
                "git",
                "rev-parse",
                "--abbrev-ref",
                "HEAD"
            )
            standardOutput = stdout
        }
        return stdout.toString().trim()
    }
}