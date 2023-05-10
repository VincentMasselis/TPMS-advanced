import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.provideDelegate

plugins {
    kotlin("kapt")
}

dependencies {
    val daggerVersion: String by project
    "implementation"("com.google.dagger:dagger:$daggerVersion")
    "kapt"("com.google.dagger:dagger-compiler:$daggerVersion")
}