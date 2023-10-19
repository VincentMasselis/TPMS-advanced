import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions { // New lazy configuration options
        jvmTarget = JVM_17
        freeCompilerArgs.addAll(
            "-Xexplicit-api=strict",
            "-opt-in=kotlin.RequiresOptIn",
            "-Xcontext-receivers" // Builds as expected but the IDE is still showing an error
        )
    }
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

dependencies {
    val agpVersion: String by project
    implementation("com.android.tools.build:gradle:$agpVersion")
    implementation(embeddedKotlin("gradle-plugin")) // Contains the plugin "org.jetbrains.kotlin.android"
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.18.0")
    implementation("com.google.apis:google-api-services-androidpublisher:v3-rev20211125-1.32.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}