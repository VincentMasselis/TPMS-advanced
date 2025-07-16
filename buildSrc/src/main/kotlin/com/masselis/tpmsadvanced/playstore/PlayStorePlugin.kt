package com.masselis.tpmsadvanced.playstore

import StricSemanticVersion
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
import org.gradle.configurationcache.extensions.capitalized
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
                if (variant.isMinifyEnabled.not())
                    return@onVariants

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
                    releaseNotesDir = project
                        .layout
                        .projectDirectory
                        .dir("src/${variant.flavorName}/play/release-notes/en-US/")
                    // Preconditions
                    releaseNotesDir.get()
                        .asFileTree
                        .firstOrNull { runCatching { StricSemanticVersion(it.nameWithoutExtension) }.isFailure }
                        ?.also { throw GradleException("This release note file name is invalid: $it") }
                    releaseNotesDir.get()
                        .asFileTree
                        .firstOrNull { it.nameWithoutExtension == ext.version.get().toString() }
                        ?: throw GradleException("The release note file associated to the version ${ext.version.get()} is missing, add it to continue: ${releaseNotesDir.get()}/${ext.version.get()}.txt")
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