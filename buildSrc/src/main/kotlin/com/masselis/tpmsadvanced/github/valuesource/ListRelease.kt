package com.masselis.tpmsadvanced.github.valuesource

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.api.tasks.Input
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class ListRelease :
    ValueSource<List<JsonObject>, ValueSourceParameters.None> {

    @get:Input
    abstract val githubToken: Property<String>

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun obtain(): List<JsonObject> = ByteArrayOutputStream()
        .also { stdout ->
            execOperations.exec {
                commandLine(
                    "curl", "-L",
                    "-H", "Accept: application/vnd.github+json",
                    "-H", "Authorization: Bearer ${githubToken.get()}",
                    "-H", "X-GitHub-Api-Version: 2022-11-28",
                    "https://api.github.com/repos/VincentMasselis/TPMS-advanced/releases",
                )
                standardOutput = stdout
            }
        }
        .use { it.toString() }
        .let(Json::parseToJsonElement)
        .jsonArray
        .map { it.jsonObject }
}