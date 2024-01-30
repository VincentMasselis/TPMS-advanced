package com.masselis.tpmsadvanced.gitflow

import org.gradle.kotlin.dsl.from
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

internal abstract class AssertCurrentBranch : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:Inject
    protected abstract val providerFactory: ProviderFactory

    @get:Input
    abstract val currentBranch: Property<String>

    private val realCurrentBranch
        get() = providerFactory.from(CurrentBranchValueSource::class)

    init {
        group = "verification"
        description = "Check the current branch is \"currentBranch\""
    }

    @TaskAction
    internal fun process() {
        if (realCurrentBranch.get() != currentBranch.get())
            throw GradleException("Current branch is \"$realCurrentBranch\" but \"${currentBranch.get()}\" was expected")
    }
}