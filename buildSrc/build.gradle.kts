@file:Suppress("UnstableApiUsage")

import org.gradle.api.internal.artifacts.dependencies.DefaultMinimalDependency
import org.gradle.api.internal.artifacts.dependencies.DefaultMutableMinimalDependency
import org.gradle.api.internal.artifacts.dependencies.DefaultMutableVersionConstraint
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.Companion.DEFAULT
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.utils.sure

plugins {
    `kotlin-dsl`
    alias(libs.plugins.serialization) version embeddedKotlinVersion
}

// Replaces the default configuration applied by KotlinDslCompilerPlugins.kt
kotlinDslPluginOptions {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            // Uses the embedded kotlin api and language version instead of the default language
            // version from the KotlinDSL plugin
            apiVersion = DEFAULT
            languageVersion = DEFAULT
            freeCompilerArgs.addAll(
                "-Xexplicit-api=strict",
                "-opt-in=kotlin.RequiresOptIn",
                "-Xcontext-receivers", // Builds as expected but the IDE is still showing an error,
            )
        }
    }
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(kotlin("gradle-plugin", libs.versions.kotlin.get()))
    implementation(libs.compose.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)
    implementation(libs.google.oauth2.http)
    implementation(libs.google.android.publisher)
    implementation(libs.kotlinx.serialization.map {
        DefaultMinimalDependency(
            it.module,
            // Comparison looks naive but it works ! Check by yourself: https://pl.kotl.in/He08juvcR
            if (embeddedKotlinVersion >= "2")
                DefaultMutableVersionConstraint(it.versionConstraint)
            else
                DefaultMutableVersionConstraint(libs.versions.serialization.json.kotlin1.get())
        )
    })
    implementation(
        libs.ksp.gradle.plugin.get()
            .copy()
            .apply { version { prefer("${libs.versions.kotlin.get()}-${libs.versions.ksp.get()}") } }
    )
    implementation(libs.paparazzi.gradle.plugin)
    implementation(libs.jadx)

    // https://github.com/gradle/gradle/issues/15383
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

gradlePlugin {
    plugins {
        register("ApkAnalysePlugin") {
            id = "com.masselis.tpmsadvanced.obfuscation.assertions.app"
            implementationClass = "com.masselis.tpmsadvanced.analyse.AppPlugin"
        }
        register("ApkAnalyseModulePlugin") {
            id = "com.masselis.tpmsadvanced.obfuscation.assertions.watcher"
            implementationClass = "com.masselis.tpmsadvanced.analyse.LibraryPlugin"
        }
    }
}