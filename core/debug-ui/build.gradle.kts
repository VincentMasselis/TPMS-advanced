plugins {
    `android-lib`
}

android {
    namespace = "com.masselis.tpmsadvanced.core.debugui"
}

dependencies {
    debugApi(project(":core:common"))
    // Tooling support (Previews, etc.)
    debugApi(libs.androidx.compose.ui.tooling)
    debugApi(libs.androidx.compose.ui.manifest)
    debugApi(libs.mockk.android)
}