package com.masselis.tpmsadvanced.gitflow

import CommitSha
import SemanticVersion
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.masselis.tpmsadvanced.gitflow.task.AssertBranchIsUnique
import com.masselis.tpmsadvanced.gitflow.task.AssertCurrentBranch
import com.masselis.tpmsadvanced.gitflow.task.AssertGitDiffIsEmpty
import com.masselis.tpmsadvanced.gitflow.task.AssertNearestParent
import com.masselis.tpmsadvanced.gitflow.task.AssertNoCommitDiff
import com.masselis.tpmsadvanced.gitflow.task.AssertTagIsUnique
import com.masselis.tpmsadvanced.gitflow.task.CreateBranch
import com.masselis.tpmsadvanced.gitflow.task.TagCommit
import com.masselis.tpmsadvanced.gitflow.valuesource.CommitCountBetweenBranch
import com.masselis.tpmsadvanced.gitflow.valuesource.CurrentBranch
import com.masselis.tpmsadvanced.gitflow.valuesource.VersionCode
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.from
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.register

public class GitflowPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        val currentReleaseTag = objects.property<SemanticVersion>()
        val lastReleaseCommitSha = objects.property<String>()
        val ext = extensions.create<GitflowExtension>(
            "gitflow",
            currentReleaseTag,
            lastReleaseCommitSha
        )

        val currentBranch = providers.from(CurrentBranch::class)
        val releaseBuildCount = providers.from(CommitCountBetweenBranch::class) {
            fromBranch = ext.developBranch
            toBranch = ext.releaseBranch
        }
        val versionCode = providers.from(VersionCode::class) {
            version = ext.version
            this.currentBranch = currentBranch
            releaseBranch = ext.releaseBranch
            mainBranch = ext.mainBranch
            this.releaseBuildCount = releaseBuildCount
        }
        currentReleaseTag.set(versionCode.flatMap { vc ->
            if (currentBranch.get() == ext.releaseBranch.get())
                provider { SemanticVersion("${ext.version.get()}+vc$vc") }
            else
                ext.version.map { SemanticVersion(it) }
        })

        lastReleaseCommitSha.set(providers.from(CommitSha::class) {
            argument = currentBranch.flatMap { currentBranch ->
                when (currentBranch) {
                    // We're working on main, latest release was the previous commit on main
                    ext.mainBranch.get() -> ext.mainBranch.map { "$it^1" }

                    // We're working on release, if this is the first commit of this branch, the
                    // latest release is main, if not, the latest release is the previous commit
                    // from the current release branch
                    ext.releaseBranch.get() -> releaseBuildCount.flatMap { commitCount ->
                        if (commitCount == 0) ext.mainBranch
                        else ext.releaseBranch.map { "$it^1" }
                    }

                    else -> providers.provider { throw GradleException("Cannot compute the latest release commit because the current branch is not a releasable branch. Current branch \"$currentBranch\"") }
                }
            }
        })
        subprojects {
            plugins.all {
                if (this is LibraryPlugin) configure<BaseExtension> {
                    afterEvaluate {
                        if (buildFeatures.buildConfig == true) productFlavors.all {
                            buildConfigField(
                                "int",
                                "VERSION_CODE",
                                versionCode.get().toString()
                            )
                            buildConfigField(
                                "String",
                                "VERSION_NAME",
                                "\"${currentReleaseTag.get()}\""
                            )
                        }
                    }
                }
                if (this is AppPlugin) configure<BaseAppModuleExtension> {
                    defaultConfig.versionCode = versionCode.get()
                    defaultConfig.versionName = currentReleaseTag.get().toString()
                }
            }
        }

        val assertGitDiffIsEmpty = tasks.register<AssertGitDiffIsEmpty>("assertGitDiffIsEmpty")

        // A release branch must:
        // - Start from develop
        // - No tag with the same version exists and no branch hotfix with the same version exists too
        // - Being up to date with develop and main

        // Release branch creation
        val assertCurrentBranchIsDevelop =
            tasks.register<AssertCurrentBranch>("assertCurrentBranchIsDevelop") {
                expectedBranch = ext.developBranch
            }
        val assertProductionTagWasNotCreatedYet =
            tasks.register<AssertTagIsUnique>("assertProductionTagWasNotCreatedYet") {
                tagFilter = ext.version.map { it.toString() }
            }
        val assertHotfixBranchWasNotCreatedYet =
            tasks.register<AssertBranchIsUnique>("assertVersionedBranchWasNotCreatedYet") {
                branchFilter = ext.version.map { "hotfix/$it" }
            }
        val assertDevelopIsUpToDateWithMain =
            tasks.register<AssertNoCommitDiff>("assertDevelopIsUpToDateWithMain") {
                fromBranch = ext.mainBranch
                toBranch = ext.developBranch
            }
        tasks.register<CreateBranch>("createRelease") {
            dependsOn(
                assertCurrentBranchIsDevelop,
                assertGitDiffIsEmpty,
                assertProductionTagWasNotCreatedYet, assertHotfixBranchWasNotCreatedYet,
                assertDevelopIsUpToDateWithMain,
            )
            branch = ext.releaseBranch.map {
                // Ignores the <remote> part of the branch name
                // More info: https://git-scm.com/book/en/v2/Git-Branching-Remote-Branches
                it.substringAfter('/')
            }
        }
        // Release branch post-creation checks
        val assertCurrentBranchIsRelease =
            tasks.register<AssertCurrentBranch>("assertCurrentBranchIsRelease") {
                expectedBranch = ext.releaseBranch
            }
        val assertReleaseSourceIsDevelop =
            tasks.register<AssertNearestParent>("assertReleaseSourceIsDevelop") {
                dependsOn(assertCurrentBranchIsRelease)
                parentBranch = ext.developBranch
                this.currentBranch = ext.releaseBranch
            }
        tasks.register("assertReleaseBranchIsValid") {
            dependsOn(
                assertReleaseSourceIsDevelop,
                assertProductionTagWasNotCreatedYet, assertHotfixBranchWasNotCreatedYet,
                assertDevelopIsUpToDateWithMain,
            )
        }

        // A hotfix branch must:
        // - Start form main
        // - No tag with the same version exists
        // - Being up to date with main

        // Hotfix branch creation
        val assertCurrentBranchIsMain =
            tasks.register<AssertCurrentBranch>("assertCurrentBranchIsMain") {
                expectedBranch = ext.mainBranch
            }
        tasks.register<CreateBranch>("createHotfix") {
            dependsOn(
                assertCurrentBranchIsMain,
                assertGitDiffIsEmpty,
                assertProductionTagWasNotCreatedYet,
            )
            branch = ext.releaseBranch.map {
                // Ignores the <remote> part of the branch name
                // More info: https://git-scm.com/book/en/v2/Git-Branching-Remote-Branches
                it.substringAfter('/')
            }
        }
        // Hotfix branch post-creation checks
        val assertCurrentBranchIsHotfix =
            tasks.register<AssertCurrentBranch>("assertCurrentBranchIsHotfix") {
                expectedBranch = ext.hotfixBranch
            }
        val assertHotfixSourceIsMain =
            tasks.register<AssertNearestParent>("assertHotfixSourceIsMain") {
                dependsOn(assertCurrentBranchIsHotfix)
                parentBranch = ext.mainBranch
                this.currentBranch = ext.hotfixBranch
            }
        val assertHotfixIsUpToDateWithMain =
            tasks.register<AssertNoCommitDiff>("assertHotfixIsUpToDateWithMain") {
                fromBranch = ext.mainBranch
                toBranch = ext.hotfixBranch
            }
        tasks.register("assertHotfixBranchIsValid") {
            dependsOn(
                assertHotfixSourceIsMain,
                assertProductionTagWasNotCreatedYet,
                assertHotfixIsUpToDateWithMain,
            )
        }

        // A main commit must:
        // - No tag with the same version exists
        tasks.register<AssertTagIsUnique>("assertVersionWasNotPushInProductionYet") {
            tagFilter = ext.version.map { it.toString() }
        }

        tasks.register<TagCommit>("tagCommitWithCurrentVersion") {
            tag = currentReleaseTag
        }
    }
}