package com.masselis.tpmsadvanced.gitflow.task

import com.masselis.tpmsadvanced.gitflow.valuesource.WorkingTree
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.from
import javax.inject.Inject

/** Asserts the working tree has no staged changes, unstaged changes, or untracked files. */
internal abstract class AssertWorkingTreeIsClean : DefaultTask() {

    @get:Inject
    protected abstract val providerFactory: ProviderFactory

    private val workingTree get() = providerFactory.from(WorkingTree::class)

    init {
        group = "gitflow"
        description = "Asserts the working tree has no staged or unstaged changes"
    }

    @TaskAction
    internal fun process(): Unit = workingTree.get().let { state ->
        if (state.isClean().not())
            throw GradleException("Cannot continue, the working tree is not clean (${state.describeDirty()})")
    }
}
