plugins {
    `android-lib`
    alias(libs.plugins.metro)
}

android {
    namespace = "com.masselis.tpmsadvanced.data.app"
    buildFeatures   {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:ui"))
}