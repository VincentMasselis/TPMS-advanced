package com.masselis.tpmsadvanced.gitflow.task

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

internal abstract class AssertBranchIsUnique : DefaultTask() {

    @get:Inject
    protected abstract val providerFactory: ProviderFactory

    @get:Input
    abstract val branchFilter: Property<String>

    private val branchesList
        get() = providerFactory.from(GitBranchList::class) {
            inputFilter = this@AssertBranchIsUnique.branchFilter
        }

    init {
        group = "gitflow"
        description = "Checks the branch was not created yet"
    }

    @TaskAction
    internal fun process() {
        val branchesList = branchesList.get()
        if (branchesList.isNotEmpty())
            throw GradleException("A branch named \"${branchesList.joinToString()}\" already exists")
    }
}
