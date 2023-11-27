import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

println("Embedded Kotlin version: $embeddedKotlinVersion")

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget = JVM_17
        freeCompilerArgs.addAll(
            "-Xexplicit-api=strict",
            "-opt-in=kotlin.RequiresOptIn",
            "-Xcontext-receivers", // Builds as expected but the IDE is still showing an error,
        )
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
}