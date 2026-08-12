package com.masselis.tpmsadvanced.github.task

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/**
 * Opens (or reuses) a pull request merging [source] into [target] and enables GitHub's auto-merge
 * with a merge-commit strategy, so a production push automatically flows back into develop
 * without a human remembering to do it.
 *
 * REST has no "enable auto-merge" endpoint, and no way to pin the merge method at all - only the
 * `enablePullRequestAutoMerge` GraphQL mutation supports `mergeMethod: MERGE` (as opposed to
 * squash/rebase, which would each retire one of the two branches). Both calls are plain `curl` via
 * `ExecOperations`.
 */
internal abstract class OpenBackMergePullRequest : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:Input
    abstract val githubToken: Property<String>

    @get:Input
    abstract val source: Property<String>

    @get:Input
    abstract val target: Property<String>

    init {
        group = "publishing"
        description =
            "Opens a pull request merging \"source\" into \"target\" with merge-commit auto-merge enabled"
    }

    @TaskAction
    internal fun process() = curl(
        "-X", "POST",
        "https://api.github.com/repos/VincentMasselis/TPMS-advanced/pulls",
        "-d",
        JsonObject(
            mapOf(
                "title" to JsonPrimitive("chore(gitflow): back-merge ${source.get()} into ${target.get()}"),
                "head" to JsonPrimitive(source.get()),
                "base" to JsonPrimitive(target.get()),
                "body" to JsonPrimitive(
                    "Automated git-flow back-merge. Merge as a merge commit only - " +
                            "both branches must stay alive."
                ),
            )
        ).toString(),
    )
        .let { Json.decodeFromString<JsonObject>(it) }
        .let { prCreationResponse ->
            val errorMessages = prCreationResponse["errors"]
                ?.jsonArray
                .orEmpty()
                .mapNotNull { it.jsonObject["message"]?.jsonPrimitive?.content }
            when {
                prCreationResponse["node_id"]?.jsonPrimitive?.contentOrNull != null ->
                    prCreationResponse["node_id"]?.jsonPrimitive?.content

                errorMessages.any { "No commits between" in it } -> {
                    logger.lifecycle("Nothing to back-merge: \"${source.get()}\" and \"${target.get()}\" are already in sync")
                    null
                }

                errorMessages.any { "A pull request already exists" in it } ->
                    curl(
                        "-X", "GET",
                        "https://api.github.com/repos/VincentMasselis/TPMS-advanced/pulls" +
                                "?head=VincentMasselis:${source.get()}&base=${target.get()}&state=open",
                    ).let { Json.decodeFromString<JsonArray>(it) }
                        .let { prSearchResponse ->
                            prSearchResponse.singleOrNull()
                                ?.jsonObject["node_id"]
                                ?.jsonPrimitive
                                ?.content
                                ?: error("Failed to find the existing pull request: $prSearchResponse")
                        }

                else -> error("Failed to open the back-merge pull request: $prCreationResponse")
            }
        }
        ?.let { pullRequestNodeId ->
            curl(
                "-X", "POST",
                "https://api.github.com/graphql",
                "-d",
                JsonObject(
                    mapOf(
                        "query" to JsonPrimitive(
                            """
                                        mutation {
                                            enablePullRequestAutoMerge(input: {pullRequestId: "$pullRequestNodeId", mergeMethod: MERGE}) {
                                                pullRequest { number }
                                            }
                                        }
                                        """.trimIndent()
                        )
                    )
                ).toString(),
            )
        }
        ?.also { autoMergeResponse ->
            // GraphQL returns HTTP 200 even on a logical failure - curl's exit code proves
            // nothing, the "errors" field must be checked explicitly.
            check(Json.decodeFromString<JsonObject>(autoMergeResponse).containsKey("errors").not()) {
                "Failed to enable auto-merge: $autoMergeResponse"
            }
        }

    private fun curl(vararg args: String): String = ByteArrayOutputStream()
        .also { stdout ->
            execOperations.exec {
                commandLine(
                    listOf(
                        "curl", "-L",
                        "-H", "Accept: application/vnd.github+json",
                        "-H", "Authorization: Bearer ${githubToken.get()}",
                        "-H", "X-GitHub-Api-Version: 2022-11-28",
                    ) + args
                )
                standardOutput = stdout
            }
        }
        .use { it.toString() }
}
