plugins {
    `android-lib`
    compose
    dagger
}

android {
    namespace = "com.masselis.tpmsadvanced.feature.unit"
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:common"))
    implementation(project(":core:debug-ui"))
    implementation(project(":data:unit"))
}