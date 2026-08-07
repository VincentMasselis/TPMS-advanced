package com.masselis.tpmsadvanced.gitflow

import CommitSha
import SemanticVersion
import com.masselis.tpmsadvanced.gitflow.model.HeadState
import com.masselis.tpmsadvanced.gitflow.task.AssertBranchHasNoForeignCommits
import com.masselis.tpmsadvanced.gitflow.task.AssertBranchIsAncestor
import com.masselis.tpmsadvanced.gitflow.task.AssertBranchIsUnique
import com.masselis.tpmsadvanced.gitflow.task.AssertBranchProvenance
import com.masselis.tpmsadvanced.gitflow.task.AssertCurrentBranch
import com.masselis.tpmsadvanced.gitflow.task.AssertReleaseNoteExists
import com.masselis.tpmsadvanced.gitflow.task.AssertTagIsUnique
import com.masselis.tpmsadvanced.gitflow.task.AssertWorkingTreeIsClean
import com.masselis.tpmsadvanced.gitflow.task.BumpVersionAndCommit
import com.masselis.tpmsadvanced.gitflow.task.CreateBranch
import com.masselis.tpmsadvanced.gitflow.task.FetchGitRefs
import com.masselis.tpmsadvanced.gitflow.task.PushGitflowBranch
import com.masselis.tpmsadvanced.gitflow.task.TagCommit
import com.masselis.tpmsadvanced.gitflow.task.WriteReleaseNoteIfSupplied
import com.masselis.tpmsadvanced.gitflow.valuesource.CommitCountBetweenBranch
import com.masselis.tpmsadvanced.gitflow.valuesource.CurrentBranch
import com.masselis.tpmsadvanced.gitflow.valuesource.VersionCode
import com.masselis.tpmsadvanced.gitflow.version.BumpType
import com.masselis.tpmsadvanced.gitflow.version.BumpType.PATCH
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.assign
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
            lastReleaseCommitSha,
        )

        // Every consumer of a branch name has to pick one of two things: "which local branch am I
        // on" (a LocalBranch, e.g. `release/1.6.0`) or "what has actually been pushed" (a
        // remote-tracking ref, e.g. `origin/release/1.6.0`). ext.*Branch are always the former;
        // this helper is the only place a "origin/" prefix ever gets glued on.
        fun Provider<String>.asRemoteRef(): Provider<String> =
            ext.remote.zip(this) { remote, branch -> "$remote/$branch" }

        val developRef = ext.developBranch.asRemoteRef()
        val mainRef = ext.mainBranch.asRemoteRef()
        val releaseRef = ext.releaseBranch.asRemoteRef()
        val hotfixRef = ext.hotfixBranch.asRemoteRef()

        // Empty string never matches a real branch name - a safe "detached HEAD" sentinel for the
        // plain string comparisons below (VersionCode, currentReleaseTag, lastReleaseCommitSha).
        val currentBranchName: Provider<String> = providers.from(CurrentBranch::class).map {
            when (it) {
                is HeadState.OnBranch -> it.branch
                is HeadState.Detached -> ""
            }
        }

        val releaseBuildCount = providers.from(CommitCountBetweenBranch::class) {
            fromBranch = developRef
            toBranch = releaseRef
        }
        val versionCode = providers.from(VersionCode::class) {
            version = ext.version
            this.currentBranch = currentBranchName
            releaseBranch = ext.releaseBranch
            mainBranch = ext.mainBranch
            this.releaseBuildCount = releaseBuildCount
        }
        currentReleaseTag.set(versionCode.flatMap { vc ->
            if (currentBranchName.get() == ext.releaseBranch.get())
                provider { SemanticVersion("${ext.version.get()}+vc$vc") }
            else
                ext.version.map { SemanticVersion(it) }
        })
        lastReleaseCommitSha.set(providers.from(CommitSha::class) {
            argument = currentBranchName.flatMap { branch ->
                when (branch) {
                    // We're working on main, latest release was the previous commit on main
                    ext.mainBranch.get() -> mainRef.map { "$it^1" }

                    // We're working on release, if this is the first commit of this branch, the
                    // latest release is main, if not, the latest release is the previous commit
                    // from the current release branch
                    ext.releaseBranch.get() -> releaseBuildCount.flatMap { commitCount ->
                        if (commitCount == 0) mainRef else releaseRef.map { "$it^1" }
                    }

                    else -> providers.provider {
                        throw GradleException(
                            "Cannot compute the latest release commit because the current " +
                                    "branch is not a releasable branch. Current branch \"$branch\""
                        )
                    }
                }
            }
        })

        val versionCatalogFile = layout.projectDirectory.file("gradle/libs.versions.toml")
        val releaseNoteText = providers.gradleProperty("gitflow.releaseNote")
        val releaseBumpType: Provider<BumpType> = providers
            .gradleProperty("gitflow.bump")
            .map { BumpType.fromWorkflowDispatch(it) }
        val hotfixBumpType: Provider<BumpType> = releaseBumpType
            .map { type ->
                check(type == PATCH) { "createHotfix always bumps patch; the \"-Pgitflow.bump\" argument is not allowed here" }
                type
            }
            .orElse(PATCH)

        val fetchGitRefs = tasks.register<FetchGitRefs>("fetchGitRefs") {
            remote = ext.remote
        }
        val assertWorkingTreeIsClean =
            tasks.register<AssertWorkingTreeIsClean>("assertWorkingTreeIsClean")

        // A release branch must:
        // - Start from develop
        // - No tag with the same version exists and no branch (release or hotfix) with the same
        //   version exists either
        // - main must already be fully merged into develop

        val assertCurrentBranchIsDevelop =
            tasks.register<AssertCurrentBranch>("assertCurrentBranchIsDevelop") {
                expectedBranch = ext.developBranch
            }
        val assertVersionIsUnreleased =
            tasks.register<AssertTagIsUnique>("assertVersionIsUnreleased") {
                tagFilter = ext.version.map { it.toString() }
            }
        val assertReleaseBranchIsAvailable =
            tasks.register<AssertBranchIsUnique>("assertReleaseBranchIsAvailable") {
                branchFilter = releaseRef
            }
        val assertHotfixBranchIsAvailable =
            tasks.register<AssertBranchIsUnique>("assertHotfixBranchIsAvailable") {
                branchFilter = hotfixRef
            }
        val assertDevelopContainsMain =
            tasks.register<AssertBranchIsAncestor>("assertDevelopContainsMain") {
                ancestor = mainRef
                descendant = developRef
            }
        val writeReleaseNoteIfSupplied =
            tasks.register<WriteReleaseNoteIfSupplied>("writeReleaseNoteIfSupplied") {
                versionCatalog = versionCatalogFile
                bumpType = releaseBumpType
                noteText = releaseNoteText
                this.releaseNotesDir = ext.releaseNotesDir
            }
        val assertReleaseNoteExists =
            tasks.register<AssertReleaseNoteExists>("assertReleaseNoteExists") {
                // Ordered, not depended-on: an assertion doesn't "require" the write task, it just
                // needs to run after it so a workflow_dispatch-supplied note is there to check.
                mustRunAfter(writeReleaseNoteIfSupplied)
                versionCatalog = versionCatalogFile
                bumpType = releaseBumpType
                this.releaseNotesDir = ext.releaseNotesDir
            }

        val createReleaseBranch = tasks.register<CreateBranch>("createReleaseBranch") {
            dependsOn(
                fetchGitRefs,
                assertCurrentBranchIsDevelop,
                assertWorkingTreeIsClean,
                assertVersionIsUnreleased,
                assertReleaseBranchIsAvailable,
                assertHotfixBranchIsAvailable,
                assertDevelopContainsMain,
                writeReleaseNoteIfSupplied,
                assertReleaseNoteExists,
            )
            branch = ext.releaseBranch
        }
        val bumpReleaseVersion = tasks.register<BumpVersionAndCommit>("bumpReleaseVersion") {
            dependsOn(createReleaseBranch)
            versionCatalog = versionCatalogFile
            // No fallback here on purpose: BumpVersionAndCommit requires this and reports a clear
            // error itself if it's absent when the task actually runs.
            bumpType = releaseBumpType
        }
        tasks.register("createRelease") {
            group = "gitflow"
            description = "Cuts a new release branch from develop and bumps its version"
            dependsOn(bumpReleaseVersion)
        }

        // Release branch post-creation checks
        val assertCurrentBranchIsRelease =
            tasks.register<AssertCurrentBranch>("assertCurrentBranchIsRelease") {
                expectedBranch = ext.releaseBranch
            }
        val assertReleaseBranchPointIsDevelop =
            tasks.register<AssertBranchProvenance>("assertReleaseBranchPointIsDevelop") {
                dependsOn(assertCurrentBranchIsRelease)
                subjectBranch = releaseRef
                developBranch = developRef
                mainBranch = mainRef
            }
        val assertReleaseHasNoForeignCommits =
            tasks.register<AssertBranchHasNoForeignCommits>("assertReleaseHasNoForeignCommits") {
                dependsOn(assertCurrentBranchIsRelease)
                remote = ext.remote
                subjectBranch = releaseRef
                baseBranch = developRef
            }
        tasks.register("assertReleaseBranchIsValid") {
            group = "gitflow"
            description = "Checks the release branch follows the gitflow branching model"
            dependsOn(
                assertReleaseBranchPointIsDevelop,
                assertReleaseHasNoForeignCommits,
                assertVersionIsUnreleased,
                assertHotfixBranchIsAvailable,
                assertDevelopContainsMain,
            )
        }

        // A hotfix branch must:
        // - Start from main
        // - No tag with the same version exists
        // - main must already be fully merged into it, and it must contain no commit exclusive to
        //   any other branch (including develop - the dangerous case of hotfixing unreleased work)

        val assertCurrentBranchIsMain =
            tasks.register<AssertCurrentBranch>("assertCurrentBranchIsMain") {
                expectedBranch = ext.mainBranch
            }
        val writeHotfixReleaseNoteIfSupplied =
            tasks.register<WriteReleaseNoteIfSupplied>("writeHotfixReleaseNoteIfSupplied") {
                versionCatalog = versionCatalogFile
                bumpType = hotfixBumpType
                noteText = releaseNoteText
                this.releaseNotesDir = ext.releaseNotesDir
            }
        val assertHotfixReleaseNoteExists =
            tasks.register<AssertReleaseNoteExists>("assertHotfixReleaseNoteExists") {
                // Ordered, not depended-on: see assertReleaseNoteExists above.
                mustRunAfter(writeHotfixReleaseNoteIfSupplied)
                versionCatalog = versionCatalogFile
                bumpType = hotfixBumpType
                this.releaseNotesDir = ext.releaseNotesDir
            }
        val createHotfixBranch = tasks.register<CreateBranch>("createHotfixBranch") {
            dependsOn(
                fetchGitRefs,
                assertCurrentBranchIsMain,
                assertWorkingTreeIsClean,
                assertVersionIsUnreleased,
                assertHotfixBranchIsAvailable,
                assertReleaseBranchIsAvailable,
                writeHotfixReleaseNoteIfSupplied,
                assertHotfixReleaseNoteExists,
            )
            branch = ext.hotfixBranch
        }
        val bumpHotfixVersion = tasks.register<BumpVersionAndCommit>("bumpHotfixVersion") {
            dependsOn(createHotfixBranch)
            versionCatalog = versionCatalogFile
            bumpType = hotfixBumpType
        }
        tasks.register("createHotfix") {
            group = "gitflow"
            description = "Cuts a new hotfix branch from main and bumps its version"
            dependsOn(bumpHotfixVersion)
        }

        val assertCurrentBranchIsHotfix =
            tasks.register<AssertCurrentBranch>("assertCurrentBranchIsHotfix") {
                expectedBranch = ext.hotfixBranch
            }
        val assertHotfixContainsMain =
            tasks.register<AssertBranchIsAncestor>("assertHotfixContainsMain") {
                dependsOn(assertCurrentBranchIsHotfix)
                ancestor = mainRef
                descendant = hotfixRef
            }
        val assertHotfixHasNoForeignCommits =
            tasks.register<AssertBranchHasNoForeignCommits>("assertHotfixHasNoForeignCommits") {
                dependsOn(assertCurrentBranchIsHotfix)
                remote = ext.remote
                subjectBranch = hotfixRef
                baseBranch = mainRef
            }
        tasks.register("assertHotfixBranchIsValid") {
            group = "gitflow"
            description = "Checks the hotfix branch follows the gitflow branching model"
            dependsOn(
                assertHotfixHasNoForeignCommits,
                assertVersionIsUnreleased,
                assertHotfixContainsMain,
            )
        }

        // A main commit must:
        // - Come from a branch actually checked out as main
        // - No tag with the same version exists
        tasks.register<AssertTagIsUnique>("assertVersionWasNotPushInProductionYet") {
            dependsOn(assertCurrentBranchIsMain)
            tagFilter = ext.version.map { it.toString() }
        }

        tasks.register<TagCommit>("tagCommitWithCurrentVersion") {
            tag = currentReleaseTag
        }

        tasks.register<PushGitflowBranch>("pushGitflowBranch") {
            remote = ext.remote
            mustRunAfter("createRelease", "createHotfix")
        }
    }
}
