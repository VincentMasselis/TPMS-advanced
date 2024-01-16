plugins {
    id("android-lib")
    id("compose")
    id("dagger")
}

android {
    namespace = "com.masselis.tpmsadvanced.core.feature.unit"
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:common"))
    implementation(project(":core:debug-ui"))
    implementation(project(":data:unit"))
}