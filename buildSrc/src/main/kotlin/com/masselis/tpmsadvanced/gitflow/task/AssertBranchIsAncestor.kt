package com.masselis.tpmsadvanced.gitflow.task

import com.masselis.tpmsadvanced.gitflow.valuesource.IsAncestor
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.from
import javax.inject.Inject

/** Checks that [ancestor] is fully merged into [descendant]. */
internal abstract class AssertBranchIsAncestor : DefaultTask() {

    @get:Inject
    protected abstract val providerFactory: ProviderFactory

    @get:Input
    abstract val ancestor: Property<String>

    @get:Input
    abstract val descendant: Property<String>

    init {
        group = "gitflow"
        description = "Checks \"ancestor\" is fully merged into \"descendant\""
    }

    @TaskAction
    internal fun process(): Unit = providerFactory.from(IsAncestor::class) {
        this.ancestor = this@AssertBranchIsAncestor.ancestor
        this.descendant = this@AssertBranchIsAncestor.descendant
    }.get().let { isAncestor ->
        check(isAncestor) { "\"${ancestor.get()}\" is not fully merged into \"${descendant.get()}\"" }
    }
}
