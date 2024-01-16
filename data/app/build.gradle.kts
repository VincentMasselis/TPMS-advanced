plugins {
    id("android-lib")
    id("dagger")
}

android {
    namespace = "com.masselis.tpmsadvanced.data.app"
}

dependencies {
    implementation(project(":core:ui"))
}