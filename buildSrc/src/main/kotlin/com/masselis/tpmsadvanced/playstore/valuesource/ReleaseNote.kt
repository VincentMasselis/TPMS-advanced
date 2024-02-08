package com.masselis.tpmsadvanced.playstore.valuesource

import StricSemanticVersion
import com.masselis.tpmsadvanced.playstore.valuesource.ReleaseNote.Parameters
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import javax.inject.Inject

internal abstract class ReleaseNote : ValueSource<String, Parameters> {

    interface Parameters : ValueSourceParameters {
        val releaseNotesDir: DirectoryProperty
    }

    override fun obtain(): String = parameters
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
