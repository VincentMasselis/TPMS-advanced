package com.masselis.tpmsadvanced.gitflow

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.masselis.tpmsadvanced.gitflow.task.AssertCurrentBranch
import com.masselis.tpmsadvanced.gitflow.task.AssertNewBranchVersion
import com.masselis.tpmsadvanced.gitflow.task.AssertNewVersion
import com.masselis.tpmsadvanced.gitflow.task.AssertNoCommitDiff
import com.masselis.tpmsadvanced.gitflow.task.AssertParent
import com.masselis.tpmsadvanced.gitflow.task.CreateBranch
import com.masselis.tpmsadvanced.gitflow.valuesource.BranchCommitCount
import com.masselis.tpmsadvanced.gitflow.valuesource.CurrentBranch
import com.masselis.tpmsadvanced.gitflow.valuesource.VersionCode
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.from
import org.gradle.kotlin.dsl.the

public class GitflowPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val ext = project.extensions.create<GitflowExtension>("gitflow")

        val versionCode = project.providers.from(VersionCode::class) {
            versionName = ext.versionName
            currentBranch = project.providers.from(CurrentBranch::class)
            releaseBranch = ext.releaseBranch
            mainBranch = ext.mainBranch
            mainToReleaseCommitCount = project.providers.from(BranchCommitCount::class) {
                baseBranch = ext.mainBranch
                currentBranch = ext.releaseBranch
            }
        }
        project.subprojects {
            plugins.whenPluginAdded {
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
                currentBranch = ext.releaseBranch
            }
        val assertHotfixSourceIsMain =
            project.tasks.create<AssertParent>("assertHotfixSourceIsMain") {
                dependsOn(assertCurrentBranchIsHotfix)
                parentBranch = ext.mainBranch
                currentBranch = ext.hotfixBranch
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
                currentBranch = ext.releaseBranch
            }
        val assertHotfixIsUpToDateWithMain =
            project.tasks.create<AssertNoCommitDiff>("assertHotfixIsUpToDateWithMain") {
                baseBranch = ext.mainBranch
                currentBranch = ext.hotfixBranch
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
    }
}