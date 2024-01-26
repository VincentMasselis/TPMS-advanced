package com.masselis.tpmsadvanced.gitflow

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.of
import org.gradle.process.ExecOperations
import org.jetbrains.kotlin.gradle.utils.provider
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class AssertCurrentBranch : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:Inject
    protected abstract val providerFactory: ProviderFactory

    @get:Input
    abstract val currentBranch: Property<String>

    init {
        group = "verification"
        description = "Check the current branch is \"currentBranch\""
    }

    @TaskAction
    internal fun process() {
        val realCurrentBranch = providerFactory.of(CurrentBranchValueSource::class) {}.get()
        if (realCurrentBranch != currentBranch.get())
            throw GradleException("Current branch is \"$realCurrentBranch\" but \"${currentBranch.get()}\" was expected")
    }
}