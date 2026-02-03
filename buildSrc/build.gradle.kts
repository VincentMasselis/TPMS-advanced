@file:Suppress("UnstableApiUsage")

import org.gradle.api.internal.artifacts.dependencies.DefaultMinimalDependency
import org.gradle.api.internal.artifacts.dependencies.DefaultMutableVersionConstraint
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.Companion.DEFAULT
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
                "-Xcontext-parameters", // Builds as expected but the IDE is still showing an error,
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
    implementation(libs.kotlinx.serialization.json)

    // https://github.com/gradle/gradle/issues/15383
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

gradlePlugin {
    plugins {
        create("GitflowPlugin") {
            id = "gitflow"
            implementationClass = "com.masselis.tpmsadvanced.gitflow.GitflowPlugin"
        }
        create("AndroidAppPlugin") {
            id = "android-app"
            implementationClass = "AndroidAppPlugin"
        }
        create("AndroidLibPlugin") {
            id = "android-lib"
            implementationClass = "AndroidLibPlugin"
        }
        create("AndroidTestPlugin") {
            id = "android-test"
            implementationClass = "AndroidTestPlugin"
        }
        create("ComposePlugin") {
            id = "compose"
            implementationClass = "ComposePlugin"
        }
        create("DetektPlugin") {
            id = "detekt"
            implementationClass = "DetektPlugin"
        }
    }
}