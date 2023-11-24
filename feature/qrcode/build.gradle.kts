plugins {
    id("android-lib")
    id("dagger")
}

android {
    namespace = "com.masselis.tpmsadvanced.qrcode"
    enableCompose(this)
}

dependencies {
    implementation(project(":feature:core"))

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