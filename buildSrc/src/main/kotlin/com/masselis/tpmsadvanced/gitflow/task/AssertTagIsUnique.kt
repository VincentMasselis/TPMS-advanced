package com.masselis.tpmsadvanced.gitflow.task

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

internal abstract class AssertTagIsUnique : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:Inject
    protected abstract val providerFactory: ProviderFactory

    /**
     * Could be `1.3.2` to search the exact value or `1.3.2*` to search for `1.3.2` with a suffix
     */
    @get:Input
    abstract val tagFilter: Property<String>

    private val tagList
        get() = providerFactory.from(GitTagList::class) {
            inputFilter = tagFilter
        }

    init {
        group = "gitflow"
        description = "Checks this tag was not created yet"
    }

    @TaskAction
    internal fun process() {
        val tagList = tagList.get()
        if (tagList.isNotEmpty())
            throw GradleException("A tag named \"${tagList.joinToString()}\" already exists")
    }
}
