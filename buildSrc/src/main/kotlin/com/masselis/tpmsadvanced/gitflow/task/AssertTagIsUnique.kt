package com.masselis.tpmsadvanced.gitflow.task

import com.masselis.tpmsadvanced.gitflow.valuesource.GitTagList
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.from
import javax.inject.Inject

internal abstract class AssertTagIsUnique : DefaultTask() {

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
    internal fun process(): Unit = tagList.get().let { tags ->
        check(tags.isEmpty()) { "A tag named \"${tags.joinToString()}\" already exists" }
    }
}
