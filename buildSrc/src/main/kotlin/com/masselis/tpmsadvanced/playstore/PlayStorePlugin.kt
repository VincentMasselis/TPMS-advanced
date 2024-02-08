package com.masselis.tpmsadvanced.playstore

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.impl.VariantOutputImpl
import com.android.build.gradle.internal.scope.getOutputPath
import com.masselis.tpmsadvanced.playstore.task.PublishToPlayStore
import com.masselis.tpmsadvanced.playstore.task.UpdatePlayStoreScreenshots
import com.masselis.tpmsadvanced.playstore.valuesource.ReleaseNote
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.from
import org.gradle.kotlin.dsl.registerIfAbsent
import org.gradle.kotlin.dsl.the

@Suppress("UnstableApiUsage")
public class PlayStorePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val ext = project.extensions.create<PlayStoreExtension>("playStore")
        project.gradle.sharedServices.registerIfAbsent(
            "android-publisher-service", AndroidPublisherService::class
        ) {
            parameters.serviceAccountCredentials = ext.serviceAccountCredentials
        }

        project.the<ApplicationAndroidComponentsExtension>().apply {
            onVariants { variant ->
                if (variant.isMinifyEnabled.not())
                    return@onVariants

                val packageName = variant.applicationId
                val versionName = variant.outputs.single().versionName
                val releaseBundle = variant
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
                val releaseNotesDir = project
                    .layout
                    .projectDirectory
                    .dir("src/${variant.flavorName}/play/release-notes/en-US/")

                project.tasks.create<PublishToPlayStore>("publishToPlayStoreBeta${variant.name.capitalized()}") {
                    dependsOn("bundle${variant.name.capitalized()}")
                    track = "beta"
                    this.packageName = packageName
                    this.versionName = versionName
                    this.releaseBundle = releaseBundle
                    this.releaseNotesDir = releaseNotesDir
                }
                project.tasks.create<PublishToPlayStore>("publishToPlayStoreProduction${variant.name.capitalized()}") {
                    dependsOn("bundle${variant.name.capitalized()}")
                    track = "production"
                    this.packageName = packageName
                    this.versionName = versionName
                    this.releaseBundle = releaseBundle
                    this.releaseNotesDir = releaseNotesDir
                }
                project.tasks.create<UpdatePlayStoreScreenshots>("updatePlayStoreScreenshots${variant.name.capitalized()}") {
                    this.packageName = packageName
                    screenshotDirectory = project
                        .layout
                        .projectDirectory
                        .dir("src/${variant.flavorName}/play/listings/en-US/graphics/phone-screenshots")
                }
            }
        }
    }
}