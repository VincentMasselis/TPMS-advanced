package com.masselis.tpmsadvanced.gitflow.task

import StricSemanticVersion
import com.masselis.tpmsadvanced.gitflow.valuesource.GitBranchList
import com.masselis.tpmsadvanced.gitflow.valuesource.GitTagList
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.from
import org.gradle.process.ExecOperations
import javax.inject.Inject

internal abstract class AssertVersionIsUniqueFromTagsAndBranches : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:Inject
    protected abstract val providerFactory: ProviderFactory

    @get:Input
    abstract val version: Property<StricSemanticVersion>

    @get:Input
    abstract val ignoredBranches: SetProperty<String>

    private val tagList
        get() = providerFactory.from(GitTagList::class) {
            inputFilter = version.map { "$it*" }
        }

    private val branchesList
        get() = providerFactory.from(GitBranchList::class) {
            inputFilter = version.map { "*/$it" }
        }

    init {
        group = "gitflow"
        description = "Checks the version was not created as tag or branch"
    }

    @TaskAction
    internal fun process() {
        if (tagList.get().isNotEmpty())
            throw GradleException("Cannot create a new version \"${version.get()}\", a tag with this name already exists")

        val branchesList = branchesList.get().subtract(ignoredBranches.get())
        if (branchesList.isNotEmpty())
            throw GradleException("Cannot work with the version \"${version.get()}\", a branch named \"${branchesList.joinToString()}\" already exists")
    }
}
