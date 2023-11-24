plugins {
    id("android-lib")
}

android {
    namespace = "com.masselis.tpmsadvanced.core.ui"
    enableCompose(this)
}

dependencies {
    api(project(":core:common"))
    api(libs.androidx.savedstate)
    api(libs.lifecycle.livedata.ktx)
    api(libs.lifecycle.viewmodel.savedstate)
    api(libs.lifecycle.viewmodel.ktx)
    api(libs.lifecycle.viewmodel.compose)
    api(libs.lifecycle.process)


    api(libs.compose.ui)
    api(libs.compose.preview)
    api(libs.kotlinx.collection)
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    api(libs.compose.foundation)
    // Material Design
    api(libs.material3)
    api(libs.material)
    // Integration with activities
    api(libs.compose.activity)
    api(libs.androidx.activity)
    api(libs.androidx.fragment)
    // Layout
    api(libs.compose.constraintlayout)
    // Navigation
    api(libs.compose.navigation)
    // Accompanist
    api(libs.accompanist.permissions)

    implementation(project(":core:debug-ui"))

    testImplementation(project(":core:test"))
}