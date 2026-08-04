package com.masselis.tpmsadvanced.bitwarden.valuesource

import com.masselis.tpmsadvanced.bitwarden.valuesource.FetchBitwardenAttachment.Parameters
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.File
import javax.inject.Inject

internal abstract class FetchBitwardenAttachment : ValueSource<File, Parameters> {

    internal interface Parameters : ValueSourceParameters {
        val credentials: Property<BitwardenCredentials>
        val cliConfigDir: DirectoryProperty
        val itemName: Property<String>
        val outputFile: RegularFileProperty
    }

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun obtain(): File? = parameters.run {
        val cliEnv = bitwardenCliEnv(cliConfigDir.get().asFile.apply { mkdirs() })
        val session = execOperations.bwSession(credentials.get(), cliEnv)
        val item = execOperations.findBitwardenItem(itemName.get(), session, cliEnv)
        val attachmentId = item["attachments"]
            ?.jsonArray
            ?.firstOrNull()
            ?.jsonObject
            ?.get("id")
            ?.jsonPrimitive
            ?.content
            ?: throw GradleException("Bitwarden item '${itemName.get()}' has no attachment")
        val itemId = item["id"]!!.jsonPrimitive.content

        outputFile.get().asFile.also { file ->
            file.parentFile.mkdirs()
            execOperations.bw(
                listOf(
                    "get", "attachment", attachmentId,
                    "--itemid", itemId,
                    "--output", file.absolutePath,
                    "--session", session
                ),
                cliEnv
            )
        }
    }
}
