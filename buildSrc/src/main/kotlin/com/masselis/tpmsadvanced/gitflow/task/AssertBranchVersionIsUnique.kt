package com.masselis.tpmsadvanced.gitflow.task

import StricSemanticVersion
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create

@Suppress("LeakingThis")
internal abstract class AssertBranchVersionIsUnique : DefaultTask() {

    @get:Input
    abstract val versionedBranch: Property<String>

    init {
        group = "gitflow"
        description = "Check the current branch version refers to version which was not created yet"
        dependsOn(project.tasks.create<AssertVersionIsUniqueFromTagsAndBranches>("${name}Tag") {
            version = versionedBranch.map { branchName ->
                branchName.split('/')
                    .also {
                        if (it.size != 3)
                            throw GradleException("Cannot verify the branch version because the branch uses an unsupported format, branch name: \"${versionedBranch.get()}\", supported format:\"origin/branch_name/strict_semantic_version\"")
                    }
                    .last()
                    .let { StricSemanticVersion(it) }
            }
            ignoredBranches = versionedBranch.map { setOf(it) }
        })
    }
}