plugins {
    id("android-lib")
    id("dagger")
}

android {
    namespace = "com.masselis.tpmsadvanced.qrcode"
    enableCompose(this)
}

dependencies {
    api(project(":feature:core"))

    api(project(":core:common"))
    api(project(":core:ui"))

    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:18.2.0")
    implementation("androidx.camera:camera-camera2:1.2.3")
    implementation("androidx.camera:camera-lifecycle:1.2.3")
    implementation("androidx.camera:camera-mlkit-vision:1.3.0-beta01")
}