package com.masselis.tpmsadvanced.playstore

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.VariantOutputConfiguration.OutputType.SINGLE
import com.android.build.api.variant.impl.VariantOutputImpl
import com.android.build.gradle.internal.scope.getOutputPath
import com.masselis.tpmsadvanced.playstore.task.PublishToPlayStore
import com.masselis.tpmsadvanced.playstore.task.UpdatePlayStoreScreenshots
import com.masselis.tpmsadvanced.playstore.valuesource.ReleaseNote
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.from
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.registerIfAbsent

@Suppress("UnstableApiUsage")
public class PlayStorePlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        val ext = extensions.create<PlayStoreExtension>("playStore")
        gradle.sharedServices.registerIfAbsent(
            "android-publisher-service", AndroidPublisherService::class
        ) {
            parameters.serviceAccountCredentials = ext.serviceAccountCredentials
        }

        configure<ApplicationAndroidComponentsExtension> {
            onVariants { variant ->
                if (variant.name != "normalRelease")
                    return@onVariants
                if (variant.isMinifyEnabled.not())
                    throw GradleException("Release variant doesn't have minify enabled")

                project
                    .layout
                    .projectDirectory
                    .dir("src/${variant.flavorName}/play/release-notes/en-US/")
                    .also(ext.releaseNotesDir::convention)

                val packageName = variant.applicationId
                val output = variant
                    .outputs
                    .map { it as VariantOutputImpl }
                    // Learn more: https://developer.android.com/build/configure-apk-splits#configure-split
                    .single { it.outputType == SINGLE }
                val versionName = output.versionName
                val releaseBundle = output.outputFileName.map { fileName ->
                    SingleArtifact.BUNDLE.getOutputPath(
                        layout.buildDirectory,
                        variant.name,
                        forceFilename = fileName.substringBeforeLast('.') + ".aab"
                    )
                }
                val releaseNotes = providers.from(ReleaseNote::class) {
                    releaseNotesDir = ext.releaseNotesDir
                    version = ext.version
                }
                tasks.register<PublishToPlayStore>("publishToPlayStoreBeta${variant.name.capitalized()}") {
                    dependsOn("bundle${variant.name.capitalized()}")
                    track = "beta"
                    this.packageName = packageName
                    this.versionName = versionName
                    this.releaseBundle = releaseBundle
                    this.releaseNotes = releaseNotes
                }
                tasks.register<PublishToPlayStore>("publishToPlayStoreProduction${variant.name.capitalized()}") {
                    dependsOn("bundle${variant.name.capitalized()}")
                    track = "production"
                    this.packageName = packageName
                    this.versionName = versionName
                    this.releaseBundle = releaseBundle
                    this.releaseNotes = releaseNotes
                }
                tasks.register<UpdatePlayStoreScreenshots>("updatePlayStoreScreenshots${variant.name.capitalized()}") {
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