package com.masselis.tpmsadvanced.github

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.masselis.tpmsadvanced.github.task.CreateBetaRelease
import com.masselis.tpmsadvanced.github.task.CreateOrPromoteProdRelease
import com.masselis.tpmsadvanced.github.task.ForceTagCommit
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.the

@Suppress("UnstableApiUsage")
public class GithubPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val ext = project.extensions.create<GithubExtension>("github")

        project.the<ApplicationAndroidComponentsExtension>().apply {
            onVariants { variant ->
                if (variant.isMinifyEnabled.not())
                    return@onVariants

                val variantName = variant.name.capitalized()
                val tagName = ext.versionName.map { it.toString() }

                val forceTagCommit = project.tasks.create<ForceTagCommit>("forceTagCommit$variantName") {
                    tag = tagName
                }
                project.tasks.create<CreateBetaRelease>("createGithubBetaRelease$variantName") {
                    dependsOn(forceTagCommit)
                    githubToken = ext.githubToken
                    this.tagName = tagName
                    betaBranch = ext.betaBranch
                    prodBranch = ext.prodBranch
                }
                project.tasks.create<CreateOrPromoteProdRelease>("createOrPromoteGithubProdRelease$variantName") {
                    dependsOn(forceTagCommit)
                    githubToken = ext.githubToken
                    this.tagName = tagName
                    betaBranch = ext.betaBranch
                    prodBranch = ext.prodBranch
                }
            }
        }
    }
}