plugins {
    `android-lib`
    alias(libs.plugins.metro)
}

android {
    namespace = "com.masselis.tpmsadvanced.data.unit"
}

dependencies {
    implementation(project(":core:common"))
}