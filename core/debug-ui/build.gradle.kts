plugins {
    id("android-lib")
}

android {
    namespace = "com.masselis.tpmsadvanced.core.debugui"
}

dependencies {
    debugApi(project(":core:common"))
    // Tooling support (Previews, etc.)
    debugApi("androidx.compose.ui:ui-tooling")
    debugApi("androidx.compose.ui:ui-test-manifest")

    //noinspection GradleDependency Updating to 1.13.4 cause this issue https://github.com/mockk/mockk/issues/1035
    debugApi("io.mockk:mockk-android:1.13.3")
}