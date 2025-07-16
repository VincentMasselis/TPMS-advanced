package com.masselis.tpmsadvanced.github

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.masselis.tpmsadvanced.github.task.CreateRelease
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the

public class GithubPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val ext = project.extensions.create<GithubExtension>("github")

        val upsertGithubPreRelease by project.tasks.register<CreateRelease>("createGithubPreRelease") {
            dependsOn("tagCommitWithCurrentVersion")
            githubToken = ext.githubToken
            tagName = ext.currentReleaseTag
            lastReleaseCommitSha = ext.lastReleaseCommitSha
            preRelease = true
        }
        val upsertGithubRelease by project.tasks.register<CreateRelease>("createGithubRelease") {
            dependsOn("tagCommitWithCurrentVersion")
            githubToken = ext.githubToken
            tagName = ext.currentReleaseTag
            lastReleaseCommitSha = ext.lastReleaseCommitSha
            preRelease = false
        }
        project.subprojects {
            plugins.all {
                if (this is AppPlugin) the<BaseAppModuleExtension>().applicationVariants.all {
                    upsertGithubPreRelease.dependsOn("${this@subprojects.path}:assemble${name.capitalized()}")
                    upsertGithubRelease.dependsOn("${this@subprojects.path}:assemble${name.capitalized()}")
                    outputs.all {
                        upsertGithubPreRelease.assets.from(outputFile)
                        upsertGithubRelease.assets.from(outputFile)
                    }
                }
            }
        }
    }
}