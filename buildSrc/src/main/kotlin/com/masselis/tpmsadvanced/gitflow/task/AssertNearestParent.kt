package com.masselis.tpmsadvanced.gitflow.task

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class AssertNearestParent : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:Input
    abstract val currentBranch: Property<String>

    @get:Input
    abstract val parentBranch: Property<String>

    init {
        group = "gitflow"
        description = "Check the current branch's source is \"parentBranch\""
    }

    @TaskAction
    internal fun process() {
        ByteArrayOutputStream()
            .also { stdout ->
                execOperations.exec {
                    commandLine(
                        "git",
                        "--no-pager",
                        "log", // Show every commit contained in a branch since the beginning
                        "--oneline", // Simplify the output
                        "--simplify-by-decoration", // Only show commit that are referred by some branch or tag
                        "--decorate-refs-exclude=refs/remotes", // Hides remotes branch in the output, this is useful if the current commit was not push to origin yet for instance
                        "${currentBranch.get()}^1", // Look for history of the current branch excluding the latest commit, by doing this, the current branch is not printed
                        "^${parentBranch.get()}^1", // Parent branch is subtracted to the output expect for the latest commit of this branch.
                    )
                    standardOutput = stdout
                }
            }
            // If the latest output is a commit from parentBranch then currentBranch inherit from
            // parentBranch but if an other branch is listed before the parentBranch, this means
            // currentBranch nearest parent is not parentBranch but this other branch.

            // Invalid output examples:
            /*
                develop -> feat/git-flow -> a commit of feat/git-flow is tagged -> feat/git-flow2
                This could fail because feat/git-flow2 nearest parent is feat/git-flow instead of develop
                ```
                1a8495f (feat/git-flow) Done
                e600b29 (tag: TO_REMOVE) Updated the github plugin to work with git-flow
                c7afa00 (develop) Bump to gradle 8.6
                ```
             */
            // Valid output:
            /*
                develop -> feat/git-flow -> a commit of feat/git-flow is tagged
                ```
                e600b29 (tag: TO_REMOVE) Updated the github plugin to work with git-flow
                c7afa00 (develop) Bump to gradle 8.6
                ```
             */
            .use { it.toString() }
            .trimIndent()
            .split('\n')
            .mapNotNull { it.substringAfter('(', "").substringBefore(')', "").ifBlank { null } }
            .filterNot { it.startsWith("tag: ") }
            .also { parents ->
                assert(parents.size == 1 && parents.single() == parentBranch.get()) {
                    throw GradleException("Expected nearest parent of \"${currentBranch.get()}\" is \"${parentBranch.get()}\" but \"${parents.joinToString()}\" was found")
                }
            }
    }
}