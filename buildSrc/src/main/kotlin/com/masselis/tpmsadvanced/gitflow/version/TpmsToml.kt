package com.masselis.tpmsadvanced.gitflow.version

import StricSemanticVersion
import org.gradle.api.GradleException
import org.tomlj.Toml
import java.io.File

/**
 * Reads and updates the `app` version in `gradle/libs.versions.toml`, under the `[versions]`
 * table.
 *
 * Parses the file with `tomlj` to validate its structure and locate the exact source line of the
 * `app` entry, then edits only that line, leaving every comment and every other entry byte for
 * byte as they were - a full TOML writer would reflow the whole ~120-line file.
 */
@Suppress("MISSING_DEPENDENCY_IN_INFERRED_TYPE_ANNOTATION_WARNING")
@JvmInline
internal value class TpmsToml(private val file: File) {

    var appVersion: StricSemanticVersion
        get() = file
            .asToml()
            .getString(KEY)
            .let { it ?: throw GradleException("\"$KEY\" is missing or is not a string in ${file.path}") }
            .let { StricSemanticVersion(it) }
        set(value) {
            file.asToml()
                .inputPositionOf(KEY)
                ?.line()
                .let { it ?: throw GradleException("Could not locate \"$KEY\" in ${file.path}") }
                .let { it - 1 }
                .let { index ->
                    file.readLines()
                        .toMutableList()
                        .also { lines ->
                            lines[index] = valuePattern
                                .replace(lines[index]) { "${it.groupValues[1]}$value${it.groupValues[2]}" }
                        }
                }
                .also { newLines -> file.writeText(newLines.joinToString("\n", postfix = "\n")) }
        }

    private fun File.asToml() = Toml
        .parse(toPath())
        .also { result ->
            if (result.hasErrors())
                throw GradleException(
                    "${file.path} is not valid TOML: ${
                        result.errors().joinToString { it.message.toString() }
                    }"
                )
        }

    private companion object {
        private const val KEY = "versions.app"
        val valuePattern = Regex("""(=\s*")[^"]*(")""")
    }
}
