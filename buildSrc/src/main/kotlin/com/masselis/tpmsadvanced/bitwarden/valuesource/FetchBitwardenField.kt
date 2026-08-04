package com.masselis.tpmsadvanced.bitwarden.valuesource

import com.masselis.tpmsadvanced.bitwarden.valuesource.FetchBitwardenField.Parameters
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import javax.inject.Inject

internal abstract class FetchBitwardenField : ValueSource<String, Parameters> {

    internal interface Parameters : ValueSourceParameters {
        val credentials: Property<BitwardenCredentials>
        val cliConfigDir: DirectoryProperty
        val itemName: Property<String>
        val fieldName: Property<String>
    }

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun obtain(): String? = parameters.run {
        val cliEnv = bitwardenCliEnv(cliConfigDir.get().asFile.apply { mkdirs() })
        val session = execOperations.bwSession(credentials.get(), cliEnv)
        execOperations.findBitwardenItem(itemName.get(), session, cliEnv)["fields"]
            ?.jsonArray
            ?.map { it.jsonObject }
            ?.singleOrNull { it["name"]?.jsonPrimitive?.content == fieldName.get() }
            ?.get("value")
            ?.jsonPrimitive
            ?.content
            ?: throw GradleException("Bitwarden item '${itemName.get()}' is missing field: ${fieldName.get()}")
    }
}
