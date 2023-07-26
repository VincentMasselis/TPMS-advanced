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
    implementation(project(":data:record"))
    implementation(project(":data:unit"))
    implementation(project(":data:app"))

    implementation(project(":core:common"))
    implementation(project(":core:ui"))

    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:18.2.0")
    implementation("androidx.camera:camera-camera2:1.2.3")
    implementation("androidx.camera:camera-lifecycle:1.2.3")
    implementation("androidx.camera:camera-mlkit-vision:1.3.0-beta01")
}