package com.masselis.tpmsadvanced.gitflow.task

import com.masselis.tpmsadvanced.gitflow.model.HeadState
import com.masselis.tpmsadvanced.gitflow.valuesource.CurrentBranch
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.from
import javax.inject.Inject

internal abstract class AssertCurrentBranch : DefaultTask() {

    @get:Inject
    protected abstract val providerFactory: ProviderFactory

    @get:Input
    abstract val expectedBranch: Property<String>

    private val head get() = providerFactory.from(CurrentBranch::class)

    init {
        group = "gitflow"
        description = "Check the current branch is \"expectedBranch\""
    }

    @TaskAction
    internal fun process(): Unit = head.get().let { state ->
        check(state is HeadState.OnBranch && state.branch == expectedBranch.get()) {
            val actual = when (state) {
                is HeadState.OnBranch -> state.branch
                is HeadState.Detached -> "detached HEAD at ${state.sha.take(12)}"
            }
            "Current branch is \"$actual\" but \"${expectedBranch.get()}\" was expected"
        }
    }
}
