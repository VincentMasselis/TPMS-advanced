package com.masselis.tpmsadvanced.analyse

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.VariantOutputConfiguration.OutputType.SINGLE
import com.android.build.api.variant.impl.VariantOutputImpl
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.registerIfAbsent

public class ApkAnalysePlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        configure<ApplicationAndroidComponentsExtension> {
            onVariants { variant ->
                val service = gradle
                    .sharedServices
                    .registerIfAbsent(
                        "${path}-decompiler-service-${variant.name}",
                        DecompilerService::class
                    ) {
                        parameters.apkToDecompile = variant
                            .outputs
                            .map { it as VariantOutputImpl }
                            // Learn more: https://developer.android.com/build/configure-apk-splits#configure-split
                            .single { it.outputType == SINGLE }
                            .outputFileName
                            .flatMap { fileName ->
                                layout.buildDirectory.file(
                                    "outputs" +
                                            "/apk" +
                                            "/${variant.flavorName!!}" +
                                            "/${variant.buildType!!}" +
                                            "/${fileName.substringBeforeLast('.') + ".apk"}"
                                )
                            }
                    }

                target
                    .tasks
                    .create<PrintClasses>("print${variant.name.capitalized()}Classes") {
                        dependsOn("assemble${variant.name.capitalized()}")
                        this.service = service
                    }
            }
        }
    }
}