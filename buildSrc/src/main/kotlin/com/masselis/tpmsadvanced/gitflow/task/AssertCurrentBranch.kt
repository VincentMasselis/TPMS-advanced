package com.masselis.tpmsadvanced.gitflow.task

import com.masselis.tpmsadvanced.gitflow.valuesource.CurrentBranch
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.from
import org.gradle.process.ExecOperations
import javax.inject.Inject

internal abstract class AssertCurrentBranch : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:Inject
    protected abstract val providerFactory: ProviderFactory

    @get:Input
    abstract val expectedBranch: Property<String>

    private val realCurrentBranch
        get() = providerFactory.from(CurrentBranch::class)

    init {
        group = "gitflow"
        description = "Check the current branch is \"expectedBranch\""
    }

    @TaskAction
    internal fun process() {
        if (realCurrentBranch.get() != expectedBranch.get())
            throw GradleException("Current branch is \"${realCurrentBranch.get()}\" but \"${expectedBranch.get()}\" was expected")
    }
}