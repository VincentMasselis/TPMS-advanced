import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
        options.freeCompilerArgs.addAll(
            listOf(
                "-Xexplicit-api=strict",
                "-opt-in=kotlin.RequiresOptIn",
            )
        )
    }
}

dependencies {
    val agpVersion: String by project
    val kotlinVersion: String by project
    implementation("com.android.tools.build:gradle:$agpVersion")
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.0-RC3")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.18.0")
    implementation("com.google.apis:google-api-services-androidpublisher:v3-rev20211125-1.32.1")
}