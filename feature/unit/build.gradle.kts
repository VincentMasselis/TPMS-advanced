plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger")
}

@Suppress("UnstableApiUsage")
android {
    namespace = "com.masselis.tpmsadvanced.core.feature.unit"
    buildFeatures.compose = true
    composeOptions {
        val composeCompilerVersion: String by project
        kotlinCompilerExtensionVersion = composeCompilerVersion
    }
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:common"))
    implementation(project(":core:debug-ui"))
    implementation(project(":data:unit"))
}