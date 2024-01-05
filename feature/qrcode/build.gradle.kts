plugins {
    id("android-lib")
    id("compose")
    id("dagger")
}

android {
    namespace = "com.masselis.tpmsadvanced.qrcode"
}

dependencies {
    implementation(project(":feature:core"))

    implementation(project(":data:vehicle"))
    implementation(project(":data:unit"))
    implementation(project(":data:app"))

    implementation(project(":core:common"))
    implementation(project(":core:ui"))

    testImplementation(project(":core:test"))

    implementation(libs.google.mlkit.barecode)
    implementation(libs.androidx.camera2.core)
    implementation(libs.androidx.camera2.lifecycle)
    implementation(libs.androidx.camera2.mlkitvision)
}