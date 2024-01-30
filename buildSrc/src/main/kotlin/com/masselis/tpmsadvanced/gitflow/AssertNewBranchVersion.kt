package com.masselis.tpmsadvanced.gitflow

import org.gradle.kotlin.dsl.from
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create
import javax.inject.Inject

@Suppress("LeakingThis")
internal abstract class AssertNewBranchVersion : DefaultTask() {

    @get:Inject
    protected abstract val providerFactory: ProviderFactory

    @get:Input
    abstract val versionedBranch: Property<String>

    init {
        group = "verification"
        description = "Check the current branch version refers to version which was not created yet"
        dependsOn(project.tasks.create<AssertNewVersion>("${name}Tag") {
            version = versionedBranch.map {
                versionedBranch.get().split('/')
                    .also { if (it.size != 2) throwError() }
                    .lastOrNull()
                    .let { it ?: throwError() }
                    .also {
                        if (providerFactory
                                .from(IsSemVerValueSource::class) { version = it }
                                .get()
                                .not()
                        ) throwError()
                    }
            }
        })
    }

    private fun throwError(): Nothing =
        throw GradleException("Cannot verify the branch version since because the branch uses an unsupported format, branch name: \"${versionedBranch.get()}\", supported format:\"branch_name/semantic_version\"")
}