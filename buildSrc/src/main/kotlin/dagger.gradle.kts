import org.gradle.kotlin.dsl.kotlin

plugins {
    kotlin("kapt")
}

dependencies {
    "implementation"(versionCatalog.findLibrary("dagger-lib").get())
    "kapt"(versionCatalog.findLibrary("dagger-compiler").get())
}