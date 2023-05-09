plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}
apply(from = "${project.rootDir}/gradle/dagger.gradle")

android {
    namespace = "com.masselis.tpmsadvanced.data.app"
}

dependencies {
    api(project(":core:ui"))
}