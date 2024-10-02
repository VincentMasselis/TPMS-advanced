plugins {
    `android-lib`
    dagger
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