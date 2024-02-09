plugins {
    `android-lib`
    compose
    dagger
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