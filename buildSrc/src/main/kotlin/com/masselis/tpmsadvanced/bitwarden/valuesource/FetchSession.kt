package com.masselis.tpmsadvanced.bitwarden.valuesource

import com.masselis.tpmsadvanced.bitwarden.BitwardenServer
import com.masselis.tpmsadvanced.bitwarden.valuesource.FetchSession.Parameters
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.File
import java.util.Optional
import javax.inject.Inject

internal abstract class FetchSession : ValueSource<Optional<String>, Parameters> {

    internal interface Parameters : ValueSourceParameters {
        val server: Property<BitwardenServer>
        val email: Property<String>
        val password: Property<String>
    }

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun obtain(): Optional<String> = parameters.run {
        val server = server.orNull
        val email = email.orNull?.ifBlank { null }
        val passwordFile = password
            .orNull
            ?.ifBlank { null }
            ?.let { pwd ->
                File.createTempFile("bw-pw", ".tmp").apply {
                    deleteOnExit()
                    setReadable(false, false)
                    setReadable(true, true)
                    setWritable(false, false)
                    setWritable(true, true)
                    writeText(pwd)
                }
            }
        if (server == null || email == null || passwordFile == null) Optional.empty()
        else Optional.of(execOperations.bwSession(server, email, passwordFile))
    }

    private fun ExecOperations.bwSession(
        server: BitwardenServer,
        email: String,
        // Using a password file avoids to send a clear password to Gradle which can be displayed in
        // the log because of the `ExecOperations.exec` command.
        password: File,
    ): String = bw(listOf("status"))
        .let { Json.parseToJsonElement(it).jsonObject["status"]?.jsonPrimitive?.content }!!
        .let { status ->
            // "bw config server" is rejected once a session already exists ("Logout required before
            // server config update"), and this CLI config dir persists across calls/builds - only set it
            // on the very first, still-unauthenticated call, never on the re-used login that follows.
            if (status == "unauthenticated") {
                bw(listOf("config", "server", server.url))
                bw(listOf("login", email, "--passwordfile", password.absolutePath, "--raw"))
            } else {
                bw(listOf("unlock", "--passwordfile", password.absolutePath, "--raw"))
            }
        }
        .also { bw(listOf("sync", "--session", it)) }
}
