package com.masselis.tpmsadvanced.gitflow

import CommitSha
import SemanticVersion
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.masselis.tpmsadvanced.gitflow.task.AssertCurrentBranch
import com.masselis.tpmsadvanced.gitflow.task.AssertNewBranchVersion
import com.masselis.tpmsadvanced.gitflow.task.AssertNewVersionTag
import com.masselis.tpmsadvanced.gitflow.task.AssertNewVersionTagAndBranch
import com.masselis.tpmsadvanced.gitflow.task.AssertNoCommitDiff
import com.masselis.tpmsadvanced.gitflow.task.AssertParent
import com.masselis.tpmsadvanced.gitflow.task.CreateBranch
import com.masselis.tpmsadvanced.gitflow.task.TagCommit
import com.masselis.tpmsadvanced.gitflow.valuesource.CommitCountBetweenBranch
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

        val currentReleaseTag = project.objects.property<SemanticVersion>()
        val lastReleaseCommitSha = project.objects.property<String>()
        val ext = project.extensions.create<GitflowExtension>(
            "gitflow",
            currentReleaseTag,
            lastReleaseCommitSha
        )
        val releaseBuildCount = project.providers.from(CommitCountBetweenBranch::class) {
            fromBranch = ext.developBranch
            toBranch = ext.releaseBranch
        }
        val versionCode = project.providers.from(VersionCode::class) {
            version = ext.version
            this.currentBranch = currentBranch
            releaseBranch = ext.releaseBranch
            mainBranch = ext.mainBranch
            this.releaseBuildCount = releaseBuildCount
        }
        currentReleaseTag.set(versionCode.flatMap {
            if (currentBranch.get() == ext.releaseBranch.get())
                project.provider { SemanticVersion("${ext.version.get()}+vc$it") }
            else
                ext.version.map { SemanticVersion(it) }
        })

        lastReleaseCommitSha.set(project.providers.from(CommitSha::class) {
            refname = currentBranch.flatMap { currentBranch ->
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

                    else -> project.providers.provider { throw GradleException("Cannot compute the latest release commit because the current branch is not a releasable branch. Current branch \"$currentBranch\"") }
                }
            }
        })
        project.subprojects {
            plugins.all {
                if (this is AppPlugin) the<BaseAppModuleExtension>().apply {
                    defaultConfig.versionCode = versionCode.get()
                    defaultConfig.versionName = currentReleaseTag.get().toString()
                }
            }
        }

        // Pre branch creation
        val assertNewReleaseAndHotfixBranchVersion =
            project.tasks.create<AssertNewVersionTagAndBranch>("assertNewReleaseAndHotfixBranchVersion") {
                version = ext.version
            }
        val assertCurrentBranchIsDevelop =
            project.tasks.create<AssertCurrentBranch>("assertCurrentBranchIsDevelop") {
                expectedBranch = ext.developBranch
            }
        val assertCurrentBranchIsRelease =
            project.tasks.create<AssertCurrentBranch>("assertCurrentBranchIsRelease") {
                expectedBranch = ext.releaseBranch
            }
        val assertCurrentBranchIsMain =
            project.tasks.create<AssertCurrentBranch>("assertCurrentBranchIsMain") {
                expectedBranch = ext.mainBranch
            }
        val assertCurrentBranchIsHotfix =
            project.tasks.create<AssertCurrentBranch>("assertCurrentBranchIsHotfix") {
                expectedBranch = ext.hotfixBranch
            }
        val assertDevelopIsUpToDateWithMain =
            project.tasks.create<AssertNoCommitDiff>("assertDevelopIsUpToDateWithMain") {
                fromBranch = ext.mainBranch
                this.toBranch = ext.developBranch
            }
        val assertHotfixIsUpToDateWithMain =
            project.tasks.create<AssertNoCommitDiff>("assertHotfixIsUpToDateWithMain") {
                fromBranch = ext.mainBranch
                this.toBranch = ext.hotfixBranch
            }

        // Helpers
        project.tasks.create<CreateBranch>("createRelease") {
            dependsOn(
                assertNewReleaseAndHotfixBranchVersion,
                assertCurrentBranchIsDevelop,
                assertDevelopIsUpToDateWithMain,
            )
            branch = ext.releaseBranch
        }
        project.tasks.create<CreateBranch>("createHotfix") {
            dependsOn(
                assertNewReleaseAndHotfixBranchVersion,
                assertCurrentBranchIsMain,
                assertHotfixIsUpToDateWithMain,
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
                fromBranch = ext.mainBranch
                this.toBranch = ext.releaseBranch
            }
        project.tasks.create("assertReleaseBranchIsValid") {
            dependsOn(
                assertReleaseSourceIsDevelop,
                assertReleaseBranchVersionIsNew,
                assertReleaseIsUpToDateWithMain,
            )
        }
        project.tasks.create("assertHotfixBranchIsValid") {
            dependsOn(
                assertHotfixSourceIsMain,
                assertHotfixBranchVersionIsNew,
                assertHotfixIsUpToDateWithMain,
            )
        }
        project.tasks.create<AssertNewVersionTag>("assertMainCommitNewVersion") {
            version = ext.version
        }
        project.tasks.create<TagCommit>("tagCommit") {
            tag = currentReleaseTag
        }
    }
}