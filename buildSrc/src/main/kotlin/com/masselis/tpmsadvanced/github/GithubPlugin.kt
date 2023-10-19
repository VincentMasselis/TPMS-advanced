package com.masselis.tpmsadvanced.github

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.impl.VariantOutputImpl
import com.android.build.gradle.internal.scope.getOutputPath
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.registerIfAbsent
import java.io.File

@Suppress("NAME_SHADOWING")
public class GithubPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create<GithubExtension>("github")

        with(project.extensions.getByType(ApplicationAndroidComponentsExtension::class)) {
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
                        tagName = versionCodeTag
                    }
                project.tasks.create<PromoteGithubRelease>("promoteGithubRelease$variantName") {
                    tagName = versionCodeTag
                }
            }
        }
    }
}