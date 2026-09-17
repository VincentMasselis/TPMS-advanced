package com.masselis.tpmsadvanced.bitwarden.valuesource

import org.gradle.api.GradleException
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import java.io.File

// A bare "bw" command name is resolved against the launching process's own PATH, fixed at JVM
// startup - widening PATH in the child's environment has no effect on that lookup. GUI-launched
// Gradle daemons (e.g. an IDE's own sync daemon) often lack Homebrew's PATH entries, so we resolve
// an absolute path ourselves instead of relying on PATH-based lookup at all.
private val bwExecutable: String by lazy {
    (listOf("/opt/homebrew/bin/bw", "/usr/local/bin/bw") + System.getenv("PATH")
        .orEmpty()
        .split(File.pathSeparator)
        .map { File(it, "bw").path })
        .firstOrNull { File(it).canExecute() }
        ?: "bw"
}

internal fun ExecOperations.bw(
    vararg args: String,
    session: String? = null,
): String = ByteArrayOutputStream().use { stdout ->
    val stderr = ByteArrayOutputStream()
    exec {
        commandLine(listOf(bwExecutable) + args)
        // Avoids the session to be visible in clear in the exec command
        if (session != null) environment("BW_SESSION", session)
        standardOutput = stdout
        errorOutput = stderr
        isIgnoreExitValue = true
    }.also { result ->
        if (result.exitValue != 0)
            throw GradleException(
                "Bitwarden CLI command failed (bw ${args.joinToString(" ")}): ${
                    stderr.toString().trim()
                }"
            )
    }
    stdout.toString().trim()
}
