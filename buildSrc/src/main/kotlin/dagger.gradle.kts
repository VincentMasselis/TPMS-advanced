import org.gradle.kotlin.dsl.kotlin

plugins {
    kotlin("kapt")
}

dependencies {
    val daggerVersion = versionsToml().getString("versions.dagger")
    "implementation"("com.google.dagger:dagger:$daggerVersion")
    "kapt"("com.google.dagger:dagger-compiler:$daggerVersion")
}