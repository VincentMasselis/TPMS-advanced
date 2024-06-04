package com.masselis.tpmsadvanced.gitflow

import CommitSha
import SemanticVersion
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.masselis.tpmsadvanced.gitflow.task.AssertBranchVersionIsUnique
import com.masselis.tpmsadvanced.gitflow.task.AssertCurrentBranch
import com.masselis.tpmsadvanced.gitflow.task.AssertNearestParent
import com.masselis.tpmsadvanced.gitflow.task.AssertNoCommitDiff
import com.masselis.tpmsadvanced.gitflow.task.AssertTagVersionIsUnique
import com.masselis.tpmsadvanced.gitflow.task.AssertVersionIsUniqueFromTagsAndBranches
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

public class GitflowPlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        val currentBranch = providers.from(CurrentBranch::class)

        val currentReleaseTag = objects.property<SemanticVersion>()
        val lastReleaseCommitSha = objects.property<String>()
        val ext = extensions.create<GitflowExtension>(
            "gitflow",
            currentReleaseTag,
            lastReleaseCommitSha
        )
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
        currentReleaseTag.set(versionCode.flatMap {
            if (currentBranch.get() == ext.releaseBranch.get())
                provider { SemanticVersion("${ext.version.get()}+vc$it") }
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

        // Pre branch creation
        val assertVersionIsUnique =
            tasks.create<AssertVersionIsUniqueFromTagsAndBranches>("assertVersionIsUnique") {
                version = ext.version
                ignoredBranches = emptySet()
            }
        val assertCurrentBranchIsDevelop =
            tasks.create<AssertCurrentBranch>("assertCurrentBranchIsDevelop") {
                expectedBranch = ext.developBranch
            }
        val assertCurrentBranchIsRelease =
            tasks.create<AssertCurrentBranch>("assertCurrentBranchIsRelease") {
                expectedBranch = ext.releaseBranch
            }
        val assertCurrentBranchIsMain =
            tasks.create<AssertCurrentBranch>("assertCurrentBranchIsMain") {
                expectedBranch = ext.mainBranch
            }
        val assertCurrentBranchIsHotfix =
            tasks.create<AssertCurrentBranch>("assertCurrentBranchIsHotfix") {
                expectedBranch = ext.hotfixBranch
            }
        val assertDevelopIsUpToDateWithMain =
            tasks.create<AssertNoCommitDiff>("assertDevelopIsUpToDateWithMain") {
                fromBranch = ext.mainBranch
                toBranch = ext.developBranch
            }

        // Helpers
        tasks.create<CreateBranch>("createRelease") {
            dependsOn(
                // Search for version saved into hotfix/*, release/* and tags to ensure the current
                // version is unique
                assertVersionIsUnique,
                // "createRelease" can be called only if the current branch comes from develop
                assertCurrentBranchIsDevelop,
                // The current release branch depends on develop which must be up-to-date when
                // creating the branch
                assertDevelopIsUpToDateWithMain,
            )
            branch = ext.releaseBranch
        }
        tasks.create<CreateBranch>("createHotfix") {
            dependsOn(
                // Search for version saved into hotfix/*, release/* and tags to ensure the current
                // version is unique
                assertVersionIsUnique,
                // "createHotfix" can be called only if the current branch comes from main
                assertCurrentBranchIsMain,
            )
            branch = ext.hotfixBranch
        }

        // Post branch creation
        val assertReleaseSourceIsDevelop =
            tasks.create<AssertNearestParent>("assertReleaseSourceIsDevelop") {
                dependsOn(assertCurrentBranchIsRelease)
                parentBranch = ext.developBranch
                this.currentBranch = ext.releaseBranch
            }
        val assertHotfixSourceIsMain =
            tasks.create<AssertNearestParent>("assertHotfixSourceIsMain") {
                dependsOn(assertCurrentBranchIsHotfix)
                parentBranch = ext.mainBranch
                this.currentBranch = ext.hotfixBranch
            }
        val assertReleaseBranchVersionIsUnique =
            tasks.create<AssertBranchVersionIsUnique>("assertReleaseBranchVersionIsUnique") {
                versionedBranch = ext.releaseBranch
            }
        val assertHotfixBranchVersionIsUnique =
            tasks.create<AssertBranchVersionIsUnique>("assertHotfixBranchVersionIsUnique") {
                versionedBranch = ext.hotfixBranch
            }
        val assertReleaseIsUpToDateWithMain =
            tasks.create<AssertNoCommitDiff>("assertReleaseIsUpToDateWithMain") {
                fromBranch = ext.mainBranch
                toBranch = ext.releaseBranch
            }
        val assertHotfixIsUpToDateWithMain =
            tasks.create<AssertNoCommitDiff>("assertHotfixIsUpToDateWithMain") {
                fromBranch = ext.mainBranch
                toBranch = ext.hotfixBranch
            }

        tasks.create("assertReleaseBranchIsValid") {
            dependsOn(
                // Ensure release branch comes from develop
                assertReleaseSourceIsDevelop,
                // Ensure the version used into the branch name was not created elsewhere as tag
                // pushed on main or as a hotfix/* branch
                assertReleaseBranchVersionIsUnique,
                // Ensure release is up-to-date with main
                assertReleaseIsUpToDateWithMain,
            )
        }
        tasks.create("assertHotfixBranchIsValid") {
            dependsOn(
                assertHotfixSourceIsMain,
                assertHotfixBranchVersionIsUnique,
                assertHotfixIsUpToDateWithMain,
            )
        }
        tasks.create<AssertTagVersionIsUnique>("assertVersionWasNotPushInProductionYet") {
            version = ext.version
        }
        tasks.create<TagCommit>("tagCommit") {
            tag = currentReleaseTag
        }
    }
}