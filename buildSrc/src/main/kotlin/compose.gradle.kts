@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.BaseExtension

plugins {
    com.android.base
    id("detekt")
}

the<BaseExtension>().apply android@{
    buildFeatures.compose = true
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    dependencies {
        "detektPlugins"(libs.compose.detekt)
    }
}