package com.masselis.tpmsadvanced.playstore

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
public class PlayStorePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val ext = project.extensions.create<PlayStoreExtension>("playStore")
        project.gradle.sharedServices.registerIfAbsent(
            "android-publisher-service", AndroidPublisherService::class
        ) {
            parameters.serviceAccountCredentials = ext.serviceAccountCredentials
        }

        with(project.extensions.getByType(ApplicationAndroidComponentsExtension::class)) {
            onVariants { variant ->
                if (variant.isMinifyEnabled.not())
                    return@onVariants

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