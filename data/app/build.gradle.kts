plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger")
}

android {
    namespace = "com.masselis.tpmsadvanced.data.app"
}

dependencies {
    api(project(":core:ui"))
}