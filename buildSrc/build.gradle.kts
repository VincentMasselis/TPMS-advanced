@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.Companion.DEFAULT
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

println("Embedded Kotlin version: $embeddedKotlinVersion")

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

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

dependencies {
    implementation(libs.android.gradle.plugin)
    // Contains the plugin "org.jetbrains.kotlin.android"
    implementation(embeddedKotlin("gradle-plugin"))
    // Uncomment the code below to uses a different version of kotlin between "buildSrc" and the main project
    //implementation(kotlin("gradle-plugin", "2.0.0"))
    implementation(libs.detekt.gradle.plugin)
    implementation(libs.google.oauth2.http)
    implementation(libs.google.android.publisher)
    implementation(libs.kotlinx.serialization)
    implementation(
        libs.ksp.gradle.plugin.get()
            .copy()
            .apply { version { prefer("$embeddedKotlinVersion-${libs.versions.ksp.get()}") } }
    )
}