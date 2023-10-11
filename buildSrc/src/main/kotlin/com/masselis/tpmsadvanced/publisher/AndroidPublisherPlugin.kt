package com.masselis.tpmsadvanced.publisher

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.impl.ApplicationVariantImpl
import com.android.build.api.variant.impl.VariantOutputImpl
import com.android.build.api.variant.impl.dirName
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
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
public class AndroidPublisherPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val ext = project.extensions.create<AndroidPublisherExtension>("androidPublisher")
        project.gradle.sharedServices.registerIfAbsent(
            "android-publisher-service", AndroidPublisherService::class
        ) {
            parameters.serviceAccountCredentials = ext.serviceAccountCredentials
        }

        with(project.extensions.getByType(ApplicationAndroidComponentsExtension::class)) {
            onVariants { variant ->
                if (variant.isMinifyEnabled.not())
                    return@onVariants

                val versionCodeTag = "vc${variant.outputs.single().versionCode.get()}"

                // Github oriented tasks
                val tagCommit = project
                    .tasks
                    .create<TagCommit>("tagCommit${variant.name.capitalized()}") {
                        tag = versionCodeTag
                    }
                project
                    .tasks
                    .create<CreateGithubRelease>("createGithubRelease${variant.name.capitalized()}") {
                        dependsOn(tagCommit)
                        tagName = versionCodeTag
                    }
                project.tasks.create<PromoteGithubRelease>("promoteGithubRelease${variant.name.capitalized()}") {
                    tagName = versionCodeTag
                }

                // Play store oriented tasks
                project.tasks.create<PublishToPlayStoreBeta>("publishToPlayStoreBeta${variant.name.capitalized()}") {
                    dependsOn("bundle${variant.name.capitalized()}")
                    packageName = variant.applicationId
                    releaseBundle = variant
                        .outputs
                        .single()
                        .let { it as VariantOutputImpl }
                        .outputFileName
                        .map { fileName ->
                            SingleArtifact
                                .BUNDLE
                                .getOutputPath(
                                    project.layout.buildDirectory,
                                    variant.name,
                                    forceFilename = fileName.substringBeforeLast(".") + ".aab"
                                )
                        }
                    releaseNotes = project
                        .layout
                        .projectDirectory
                        .file("src/${variant.flavorName}/play/release-notes/en-US/beta.txt")
                }
                project.tasks.create<PromoteToPlayStoreProduction>("promoteToPlayStoreProduction${variant.name.capitalized()}") {
                    packageName = variant.applicationId
                    currentVc = variant.outputs.single().versionCode
                }
                project.tasks.create<UpdatePlayStoreScreenshots>("updatePlayStoreScreenshots${variant.name.capitalized()}") {
                    screenshotDirectory = project
                        .layout
                        .projectDirectory
                        .dir("src/${variant.flavorName}/play/listings/en-US/graphics/phone-screenshots")
                    packageName = variant.applicationId
                }
            }
        }
    }
}