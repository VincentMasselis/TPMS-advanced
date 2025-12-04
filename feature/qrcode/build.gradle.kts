plugins {
    `android-lib`
    `android-test`
    compose
    alias(libs.plugins.metro)
}

android {
    namespace = "com.masselis.tpmsadvanced.feature.qrcode"
}

dependencies {
    implementation(project(":feature:main"))

    implementation(project(":data:vehicle"))
    implementation(project(":data:unit"))
    implementation(project(":data:app"))

    implementation(project(":core:common"))
    implementation(project(":core:ui"))

    implementation(libs.google.mlkit.barecode)
    implementation(libs.androidx.camera2.core)
    implementation(libs.androidx.camera2.lifecycle)
    implementation(libs.androidx.camera2.mlkitvision)
}