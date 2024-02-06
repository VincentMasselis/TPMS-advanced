package com.masselis.tpmsadvanced.gitflow

import CommitSha
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.masselis.tpmsadvanced.gitflow.task.AssertCurrentBranch
import com.masselis.tpmsadvanced.gitflow.task.AssertNewBranchVersion
import com.masselis.tpmsadvanced.gitflow.task.AssertNewVersion
import com.masselis.tpmsadvanced.gitflow.task.AssertNoCommitDiff
import com.masselis.tpmsadvanced.gitflow.task.TagCommit
import com.masselis.tpmsadvanced.gitflow.task.AssertParent
import com.masselis.tpmsadvanced.gitflow.task.CreateBranch
import com.masselis.tpmsadvanced.gitflow.valuesource.CommitCountSinceBase
import com.masselis.tpmsadvanced.gitflow.valuesource.CurrentBranch
import com.masselis.tpmsadvanced.gitflow.valuesource.VersionCode
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.from
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.the

public class GitflowPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val currentBranch = project.providers.from(CurrentBranch::class)

        val currentReleaseTag = project.objects.property<String>()
        val lastReleaseCommitSha = project.objects.property<String>()
        val ext = project.extensions.create<GitflowExtension>(
            "gitflow",
            currentReleaseTag,
            lastReleaseCommitSha
        )

        val versionCode = project.providers.from(VersionCode::class) {
            versionName = ext.versionName
            this.currentBranch = currentBranch
            releaseBranch = ext.releaseBranch
            mainBranch = ext.mainBranch
            this.releaseBuildCount = project.providers.from(CommitCountSinceBase::class) {
                baseBranch = ext.developBranch
            }
        }
        currentReleaseTag.set(versionCode.map { "${ext.versionName}+$it" })

        lastReleaseCommitSha.set(project.providers.from(CommitSha::class) {
            refname = currentBranch.flatMap { currentBranch ->
                when (currentBranch) {
                    // We're working on main, latest release was the previous commit on main
                    ext.mainBranch.get() -> ext.mainBranch.map { "$it^1" }

                    // We're working on release, if this is the first commit of this branch, the
                    // latest release is main, if not, the latest release is the previous commit
                    // from the current release branch
                    ext.releaseBranch.get() -> project
                        .providers
                        .from(CommitCountSinceBase::class) { baseBranch = ext.developBranch }
                        .flatMap { commitCount ->
                            if (commitCount == 0) ext.mainBranch
                            else ext.releaseBranch.map { "$it^1" }
                        }

                    else -> project.providers.provider { throw GradleException("Cannot compute the latest release commit because the current branch is not a releasable branch. Current branch \"$currentBranch\"") }
                }
            }
        })
        project.subprojects {
            plugins.all {
                if (this is AppPlugin)
                    the<BaseAppModuleExtension>().defaultConfig.versionCode = versionCode.get()
            }
        }

        // Pre branch creation
        val assertNewVersion = project.tasks.create<AssertNewVersion>("assertNewVersion") {
            version = ext.versionName
        }
        val assertCurrentBranchIsDevelop =
            project.tasks.create<AssertCurrentBranch>("assertCurrentBranchIsDevelop") {
                this.currentBranch = ext.developBranch
            }
        val assertCurrentBranchIsRelease =
            project.tasks.create<AssertCurrentBranch>("assertCurrentBranchIsRelease") {
                this.currentBranch = ext.releaseBranch
            }
        val assertCurrentBranchIsMain =
            project.tasks.create<AssertCurrentBranch>("assertCurrentBranchIsMain") {
                this.currentBranch = ext.mainBranch
            }
        val assertCurrentBranchIsHotfix =
            project.tasks.create<AssertCurrentBranch>("assertCurrentBranchIsHotfix") {
                this.currentBranch = ext.hotfixBranch
            }
        val assertDevelopIsUpToDateWithMain =
            project.tasks.create<AssertNoCommitDiff>("assertDevelopIsUpToDateWithMain") {
                baseBranch = ext.mainBranch
                this.currentBranch = ext.developBranch
            }

        // Helpers
        project.tasks.create<CreateBranch>("createRelease") {
            dependsOn(
                assertNewVersion,
                assertCurrentBranchIsDevelop,
                assertDevelopIsUpToDateWithMain,
            )
            branch = ext.releaseBranch
        }
        project.tasks.create<CreateBranch>("createHotfix") {
            dependsOn(
                assertNewVersion,
                assertCurrentBranchIsMain,
            )
            branch = ext.hotfixBranch
        }

        // Post branch creation
        val assertReleaseSourceIsDevelop =
            project.tasks.create<AssertParent>("assertReleaseSourceIsDevelop") {
                dependsOn(assertCurrentBranchIsRelease)
                parentBranch = ext.developBranch
                this.currentBranch = ext.releaseBranch
            }
        val assertHotfixSourceIsMain =
            project.tasks.create<AssertParent>("assertHotfixSourceIsMain") {
                dependsOn(assertCurrentBranchIsHotfix)
                parentBranch = ext.mainBranch
                this.currentBranch = ext.hotfixBranch
            }
        val assertHotfixBranchVersionIsNew =
            project.tasks.create<AssertNewBranchVersion>("assertHotfixBranchVersionIsNew") {
                versionedBranch = ext.hotfixBranch
            }
        val assertReleaseBranchVersionIsNew =
            project.tasks.create<AssertNewBranchVersion>("assertReleaseBranchVersionIsNew") {
                versionedBranch = ext.releaseBranch
            }
        val assertReleaseIsUpToDateWithMain =
            project.tasks.create<AssertNoCommitDiff>("assertReleaseIsUpToDateWithMain") {
                baseBranch = ext.mainBranch
                this.currentBranch = ext.releaseBranch
            }
        val assertHotfixIsUpToDateWithMain =
            project.tasks.create<AssertNoCommitDiff>("assertHotfixIsUpToDateWithMain") {
                baseBranch = ext.mainBranch
                this.currentBranch = ext.hotfixBranch
            }
        project.tasks.create("assertReleaseBranchIsValid") {
            dependsOn(
                assertReleaseSourceIsDevelop,
                assertReleaseBranchVersionIsNew,
                assertReleaseIsUpToDateWithMain,
            )
        }
        // TODO Call this task with GA
        project.tasks.create("assertHotfixBranchIsValid") {
            dependsOn(
                assertHotfixSourceIsMain,
                assertHotfixBranchVersionIsNew,
                assertHotfixIsUpToDateWithMain,
            )
        }
        project.tasks.create<TagCommit>("tagCommit") {
            this.tag = currentReleaseTag
        }
    }
}