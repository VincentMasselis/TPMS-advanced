package com.masselis.tpmsadvanced.github

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.impl.VariantOutputImpl
import com.android.build.gradle.AppPlugin
import com.masselis.tpmsadvanced.github.task.CreateRelease
import com.masselis.tpmsadvanced.github.task.OpenBackMergePullRequest
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

public class GithubPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val ext = project.extensions.create<GithubExtension>("github")

        val upsertGithubPreRelease = project.tasks.register<CreateRelease>("createGithubPreRelease") {
            dependsOn("tagCommitWithCurrentVersion")
            githubToken = ext.githubToken
            tagName = ext.currentReleaseTag
            lastReleaseCommitSha = ext.lastReleaseCommitSha
            preRelease = true
        }.get()
        val upsertGithubRelease = project.tasks.register<CreateRelease>("createGithubRelease") {
            dependsOn("tagCommitWithCurrentVersion")
            githubToken = ext.githubToken
            tagName = ext.currentReleaseTag
            lastReleaseCommitSha = ext.lastReleaseCommitSha
            preRelease = false
        }.get()
        project.tasks.register<OpenBackMergePullRequest>("openBackMergePullRequest") {
            githubToken = ext.githubToken
            source = ext.backMergeSource
            target = ext.backMergeTarget
        }
        project.subprojects {
            plugins.all {
                if (this is AppPlugin) configure<ApplicationAndroidComponentsExtension> {
                    onVariants { variant ->
                        upsertGithubPreRelease.dependsOn("${this@subprojects.path}:assemble${variant.name.capitalized()}")
                        upsertGithubRelease.dependsOn("${this@subprojects.path}:assemble${variant.name.capitalized()}")
                        variant.outputs.map { it as VariantOutputImpl }.forEach {
                            upsertGithubPreRelease.assets.from(it.outputFileName.get())
                            upsertGithubRelease.assets.from(it.outputFileName.get())
                        }
                    }
                }
            }
        }
    }
}