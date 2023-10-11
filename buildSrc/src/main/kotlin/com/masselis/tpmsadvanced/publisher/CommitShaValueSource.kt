package com.masselis.tpmsadvanced.publisher

import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

public abstract class CommitShaValueSource : ValueSource<String, CommitShaValueSource.Parameters> {

    public interface Parameters : ValueSourceParameters {
        public val backwardCount: Property<Int>
    }

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun obtain(): String {
        val stdout = ByteArrayOutputStream()
        execOperations.exec {
            commandLine(
                "git",
                "rev-parse",
                "HEAD^${parameters.backwardCount.get()}"
            )
            standardOutput = stdout
        }
        return stdout.toString().trim()
    }
}