plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

@Suppress("UnstableApiUsage")
android {
    namespace = "com.masselis.tpmsadvanced.core.ui"
    buildFeatures.compose = true
    composeOptions {
        val composeCompilerVersion: String by project
        kotlinCompilerExtensionVersion = composeCompilerVersion
    }
}

dependencies {
    val lifecycleVersion: String by project
    api(project(":core:common"))
    api("androidx.savedstate:savedstate-ktx:1.2.1")
    api("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    api("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    api("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")

    api("androidx.compose.ui:ui")
    api("androidx.compose.ui:ui-tooling-preview")
    api("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    api("androidx.compose.foundation:foundation")
    // Material Design
    api("androidx.compose.material3:material3:1.1.0-rc01")
    api("com.google.android.material:material:1.8.0")
    // Integration with activities
    api("androidx.activity:activity-compose:1.7.1")
    api("androidx.fragment:fragment-ktx:1.5.7")
    // Layout
    api("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    // Navigation
    api("androidx.navigation:navigation-compose:2.5.3")

    implementation(project(":core:debug-ui"))
}