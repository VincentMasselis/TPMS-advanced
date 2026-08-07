package com.masselis.tpmsadvanced.playstore.valuesource

import StricSemanticVersion
import com.masselis.tpmsadvanced.playstore.valuesource.ReleaseNote.Parameters
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters

internal abstract class ReleaseNote : ValueSource<String, Parameters> {

    interface Parameters : ValueSourceParameters {
        val version: Property<StricSemanticVersion>
        val releaseNotesDir: DirectoryProperty
    }

    override fun obtain(): String {
        // Preconditions
        parameters.releaseNotesDir.get()
            .asFileTree
            .firstOrNull { runCatching { StricSemanticVersion(it.nameWithoutExtension) }.isFailure }
            ?.also { throw GradleException("This release note file name is invalid: $it") }
        parameters.releaseNotesDir.get()
            .asFileTree
            .firstOrNull { it.nameWithoutExtension == parameters.version.get().toString() }
            ?: throw GradleException("The release note file associated to the version ${parameters.version.get()} is missing, add it to continue: ${parameters.releaseNotesDir.get()}/${parameters.version.get()}.txt")

        return parameters
            .releaseNotesDir
            .get()
            .asFileTree
            .sortedByDescending { StricSemanticVersion(it.nameWithoutExtension) }
            .let { files ->
                val builder = StringBuilder()
                for (file in files) {
                    val releaseNote = "v${file.nameWithoutExtension}:\n"
                        .plus(file.readText().trimIndent())
                    if ((builder.length + releaseNote.length) > 500)
                        break
                    builder.appendLine(releaseNote)
                }
                builder.toString()
            }
    }
}
