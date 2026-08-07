package com.masselis.tpmsadvanced.gitflow.task

import com.masselis.tpmsadvanced.gitflow.valuesource.CommitsExclusiveTo
import com.masselis.tpmsadvanced.gitflow.valuesource.GitBranchList
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.from
import javax.inject.Inject

/**
 * Checks that none of [subjectBranch]'s own commits (relative to [baseBranch]) are also exclusive
 * to [baseBranch] from another pushed branch's perspective - that would mean [subjectBranch] was
 * cut from, or merged with, that other branch instead of the expected parent.
 *
 * Used for both the release branch (base = develop) and the hotfix branch (base = main); in the
 * hotfix case this also catches the more dangerous mistake of cutting a hotfix from `develop`
 * instead of `main`, since develop's unreleased commits would show up as foreign.
 */
internal abstract class AssertBranchHasNoForeignCommits : DefaultTask() {

    @get:Inject
    protected abstract val providerFactory: ProviderFactory

    @get:Input
    abstract val remote: Property<String>

    @get:Input
    abstract val subjectBranch: Property<String>

    @get:Input
    abstract val baseBranch: Property<String>

    init {
        group = "gitflow"
        description = "Checks \"subjectBranch\" contains no commits exclusive to another pushed branch"
    }

    @TaskAction
    internal fun process() {
        // Guarded up front so an unpushed/fresh branch skips scanning every other branch on the
        // remote for nothing.
        val ownCommits = commitsExclusiveTo(subjectBranch.get(), baseBranch.get())
        if (ownCommits.isEmpty()) return
        providerFactory
            .from(GitBranchList::class) { inputFilter = "${remote.get()}/*" }
            .get()
            .filter { it != subjectBranch.get() && it != baseBranch.get() }
            .associateWith { commitsExclusiveTo(it, baseBranch.get()) }
            .entries
            .firstNotNullOfOrNull { (branch, theirs) -> (ownCommits intersect theirs).ifEmpty { null }?.let { branch to it } }
            ?.also { (branch, overlap) ->
                throw GradleException(
                    "\"${subjectBranch.get()}\" contains commit(s) also exclusive to \"$branch\": " +
                        "${overlap.joinToString()}. Was it cut from, or merged with, the wrong branch?"
                )
            }
    }

    private fun commitsExclusiveTo(include: String, exclude: String): Set<String> =
        providerFactory.from(CommitsExclusiveTo::class) {
            this.include = include
            excludes.add(exclude)
        }.get()
}
