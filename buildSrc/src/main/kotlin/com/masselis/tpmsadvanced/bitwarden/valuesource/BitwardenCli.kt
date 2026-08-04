package com.masselis.tpmsadvanced.bitwarden.valuesource

import com.masselis.tpmsadvanced.bitwarden.BitwardenServer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.GradleException
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.Serializable

internal data class BitwardenCredentials(
    val email: String,
    val password: String,
    val server: BitwardenServer,
) : Serializable

internal fun bitwardenCliEnv(cliConfigDir: File): Map<String, String> = mapOf(
    "BITWARDEN_CLI_CONFIG_DIR" to cliConfigDir.absolutePath
)

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
    args: List<String>,
    cliEnv: Map<String, String>
): String = ByteArrayOutputStream().use { stdout ->
    val stderr = ByteArrayOutputStream()
    exec {
        environment(cliEnv)
        commandLine(listOf(bwExecutable) + args)
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

internal fun ExecOperations.bwSession(
    credentials: BitwardenCredentials,
    cliEnv: Map<String, String>
): String {
    bw(listOf("config", "server", credentials.server.url), cliEnv)
    return bw(listOf("status"), cliEnv)
        .let { Json.parseToJsonElement(it).jsonObject["status"]?.jsonPrimitive?.content }
        .let { status ->
            if (status == "unauthenticated")
                bw(listOf("login", credentials.email, credentials.password, "--raw"), cliEnv)
            else
                bw(listOf("unlock", credentials.password, "--raw"), cliEnv)
        }
        .also { session -> bw(listOf("sync", "--session", session), cliEnv) }
}

internal fun ExecOperations.findBitwardenItem(
    itemName: String,
    session: String,
    cliEnv: Map<String, String>
) =
    bw(listOf("list", "items", "--search", itemName, "--session", session), cliEnv)
        .let { Json.parseToJsonElement(it).jsonArray }
        .map { it.jsonObject }
        .singleOrNull { it["name"]?.jsonPrimitive?.content == itemName }
        ?: throw GradleException("Bitwarden item not found or ambiguous: $itemName")
