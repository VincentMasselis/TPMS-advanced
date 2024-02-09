plugins {
    `android-lib`
    dagger
}

android {
    namespace = "com.masselis.tpmsadvanced.data.app"
}

dependencies {
    implementation(project(":core:ui"))
}