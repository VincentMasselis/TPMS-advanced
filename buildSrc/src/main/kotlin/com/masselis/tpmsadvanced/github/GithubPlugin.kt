package com.masselis.tpmsadvanced.github

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
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
                val versionCodeTag = "vc${variant.outputs.single().versionCode.get()}"

                val tagCommit = project.tasks.create<TagCommit>("tagCommit$variantName") {
                    tag = versionCodeTag
                }
                project.tasks.create<CreateGithubRelease>("createGithubRelease$variantName") {
                    dependsOn(tagCommit)
                    githubToken = ext.githubToken
                    tagName = versionCodeTag
                }
                project.tasks.create<PromoteGithubRelease>("promoteGithubRelease$variantName") {
                    githubToken = ext.githubToken
                    tagName = versionCodeTag
                }
            }
        }
    }
}