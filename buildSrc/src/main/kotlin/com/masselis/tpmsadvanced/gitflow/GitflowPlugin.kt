package com.masselis.tpmsadvanced.gitflow

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.impl.VariantOutputImpl
import com.android.build.gradle.internal.scope.getOutputPath
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.registerIfAbsent
import java.io.File
import org.gradle.kotlin.dsl.the

public class GitflowPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val ext = project.extensions.create<GitflowExtension>("gitflow")

        project.tasks.create<AssertNoCommitDiff>("assertDevelopIsUpToDateWithMain") {
            baseBranch = ext.mainBranch
            currentBranch = ext.developBranch
        }
        project.tasks.create<AssertNoCommitDiff>("assertReleaseIsUpToDateWithMain") {
            baseBranch = ext.mainBranch
            currentBranch = ext.releaseBranch
        }
        project.tasks.create<AssertNoCommitDiff>("assertHotfixIsUpToDateWithMain") {
            baseBranch = ext.mainBranch
            currentBranch = ext.hotfixBranch
        }
        project.tasks.create<AssertParent>("assertReleaseSourceIsDevelop") {
            parentBranch = ext.developBranch
            currentBranch = ext.releaseBranch
        }
        project.tasks.create<AssertParent>("assertHotfixSourceIsMain") {
            parentBranch = ext.mainBranch
            currentBranch = ext.hotfixBranch
        }
    }
}