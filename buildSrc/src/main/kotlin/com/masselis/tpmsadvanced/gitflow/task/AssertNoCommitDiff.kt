package com.masselis.tpmsadvanced.gitflow.task

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import com.masselis.tpmsadvanced.gitflow.valuesource.CommitList
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.from
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class AssertNoCommitDiff : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:Inject
    protected abstract val providerFactory: ProviderFactory

    @get:Input
    abstract val fromBranch: Property<String>

    @get:Input
    abstract val toBranch: Property<String>

    private val commitList
        get() = providerFactory.from(CommitList::class) {
            this.fromBranch = this@AssertNoCommitDiff.fromBranch
            this.toBranch = this@AssertNoCommitDiff.toBranch
        }

    init {
        group = "verification"
        description = "Check toBranch is up-to-date with fromBranch"
    }

    @TaskAction
    internal fun process() {
        val unmergedCommits = commitList.get()
        if (unmergedCommits.isNotEmpty())
            throw GradleException("Some commits from \"${fromBranch.get()}\" are missing in \"${toBranch.get()}\". Missing commits: $unmergedCommits")
    }
}