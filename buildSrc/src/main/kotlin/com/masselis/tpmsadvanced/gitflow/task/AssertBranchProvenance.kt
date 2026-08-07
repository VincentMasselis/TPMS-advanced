package com.masselis.tpmsadvanced.gitflow.task

import CommitSha
import com.masselis.tpmsadvanced.gitflow.valuesource.MergeBase
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.from
import javax.inject.Inject

/** Checks that [subjectBranch] (a release branch) was cut from [developBranch], not from [mainBranch] directly. */
internal abstract class AssertBranchProvenance : DefaultTask() {

    @get:Inject
    protected abstract val providerFactory: ProviderFactory

    @get:Input
    abstract val subjectBranch: Property<String>

    @get:Input
    abstract val developBranch: Property<String>

    @get:Input
    abstract val mainBranch: Property<String>

    init {
        group = "gitflow"
        description =
            "Checks \"subjectBranch\" was cut from \"developBranch\", not from \"mainBranch\""
    }

    @TaskAction
    internal fun process() {
        val developTip = resolve(developBranch.get())
        val mainTip = resolve(mainBranch.get())
        check(developTip != mainTip) {
            "\"${developBranch.get()}\" and \"${mainBranch.get()}\" both point at \"$developTip\"; " +
                    "there is nothing to release, and branch provenance cannot be determined from an " +
                    "identical branch point"
        }

        val mergeBaseWithDevelop = mergeBase(subjectBranch.get(), developBranch.get())
        val mergeBaseWithMain = mergeBase(subjectBranch.get(), mainBranch.get())
        check(mergeBaseWithDevelop != mergeBaseWithMain) {
            "\"${subjectBranch.get()}\" was not cut from \"${developBranch.get()}\": its merge-base " +
                    "with \"${mainBranch.get()}\" and with \"${developBranch.get()}\" are identical, " +
                    "meaning it forked directly from \"${mainBranch.get()}\""
        }
    }

    private fun mergeBase(a: String, b: String): String = providerFactory.from(MergeBase::class) {
        this.a = a
        this.b = b
    }.get()

    private fun resolve(argument: String): String = providerFactory.from(CommitSha::class) {
        this.argument = argument
    }.get()
}
