package com.masselis.tpmsadvanced.gitflow.task

import StricSemanticVersion
import com.masselis.tpmsadvanced.gitflow.valuesource.GitTagList
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.from
import org.gradle.process.ExecOperations
import javax.inject.Inject

internal abstract class AssertNewVersionTag : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:Inject
    protected abstract val providerFactory: ProviderFactory

    @get:Input
    abstract val version: Property<StricSemanticVersion>

    private val tagList
        get() = providerFactory.from(GitTagList::class) {
            inputFilter = version.map { "$it" }
        }

    init {
        group = "verification"
        description = "Checks the current version doesn't exists on main"
    }

    @TaskAction
    internal fun process() {
        if (tagList.get().isNotEmpty())
            throw GradleException("Cannot create a new version for \"${version.get()}\", a tag with the same name already exists")
    }
}
