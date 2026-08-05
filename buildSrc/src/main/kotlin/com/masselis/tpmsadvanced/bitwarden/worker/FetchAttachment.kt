package com.masselis.tpmsadvanced.bitwarden.worker

import com.masselis.tpmsadvanced.bitwarden.valuesource.bw
import com.masselis.tpmsadvanced.bitwarden.worker.FetchAttachment.Parameters
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.process.ExecOperations
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import javax.inject.Inject

internal abstract class FetchAttachment : WorkAction<Parameters> {

    internal interface Parameters : WorkParameters {
        val session: Property<String>
        val itemName: Property<String>
        val fileName: Property<String>
        val outputFile: RegularFileProperty
    }

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun execute(): Unit = with(parameters) {
        val item = execOperations.findBitwardenItem(itemName.get(), session.get())
        val attachmentId = item["attachments"]
            ?.jsonArray
            ?.firstOrNull { it.jsonObject["fileName"]?.jsonPrimitive?.content == fileName.get() }
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
                    "--session", session.get()
                ),
            )
        }
    }

    private fun ExecOperations.findBitwardenItem(
        itemName: String,
        session: String,
    ) =
        bw(listOf("list", "items", "--search", itemName, "--session", session))
            .let { Json.parseToJsonElement(it).jsonArray }
            .map { it.jsonObject }
            .singleOrNull { it["name"]?.jsonPrimitive?.content == itemName }
            ?: throw GradleException("Bitwarden item not found or ambiguous: $itemName")
}