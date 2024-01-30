package com.masselis.tpmsadvanced.gitflow

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create

public class GitflowPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val ext = project.extensions.create<GitflowExtension>("gitflow")

        // Pre branch creation
        val assertNewVersion = project.tasks.create<AssertNewVersion>("assertNewVersion") {
            version = ext.versionName
        }
        val assertCurrentBranchIsDevelop =
            project.tasks.create<AssertCurrentBranch>("assertCurrentBranchIsDevelop") {
                currentBranch = ext.developBranch
            }
        val assertCurrentBranchIsRelease =
            project.tasks.create<AssertCurrentBranch>("assertCurrentBranchIsRelease") {
                currentBranch = ext.releaseBranch
            }
        val assertCurrentBranchIsMain =
            project.tasks.create<AssertCurrentBranch>("assertCurrentBranchIsMain") {
                currentBranch = ext.mainBranch
            }
        val assertCurrentBranchIsHotfix =
            project.tasks.create<AssertCurrentBranch>("assertCurrentBranchIsHotfix") {
                currentBranch = ext.hotfixBranch
            }
        val assertDevelopIsUpToDateWithMain =
            project.tasks.create<AssertNoCommitDiff>("assertDevelopIsUpToDateWithMain") {
                baseBranch = ext.mainBranch
                currentBranch = ext.developBranch
            }

        // Helpers
        project.tasks.create<CreateBranch>("createRelease") {
            dependsOn(
                assertNewVersion,
                assertCurrentBranchIsDevelop,
                assertDevelopIsUpToDateWithMain,
            )
            branch = ext.versionName.map { "release/$it" }
        }
        project.tasks.create<CreateBranch>("createHotfix") {
            dependsOn(
                assertNewVersion,
                assertCurrentBranchIsMain,
            )
            branch = ext.versionName.map { "hotfix/$it" }
        }

        // Post branch creation
        project.tasks.create<AssertParent>("assertReleaseSourceIsDevelop") {
            dependsOn(assertCurrentBranchIsRelease)
            parentBranch = ext.developBranch
            currentBranch = ext.releaseBranch
        }
        project.tasks.create<AssertParent>("assertHotfixSourceIsMain") {
            dependsOn(assertCurrentBranchIsHotfix)
            parentBranch = ext.mainBranch
            currentBranch = ext.hotfixBranch
        }
        project.tasks.create<AssertNewBranchVersion>("assertHotfixBranchVersionIsNew") {
            versionedBranch = ext.hotfixBranch
        }
        project.tasks.create<AssertNewBranchVersion>("assertReleaseBranchVersionIsNew") {
            versionedBranch = ext.releaseBranch
        }
        project.tasks.create<AssertNoCommitDiff>("assertReleaseIsUpToDateWithMain") {
            baseBranch = ext.mainBranch
            currentBranch = ext.releaseBranch
        }
        project.tasks.create<AssertNoCommitDiff>("assertHotfixIsUpToDateWithMain") {
            baseBranch = ext.mainBranch
            currentBranch = ext.hotfixBranch
        }
    }
}