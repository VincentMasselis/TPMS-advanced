plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}
apply(from = "${project.rootDir}/gradle/dagger.gradle")

@Suppress("UnstableApiUsage")
android {
    namespace = "com.masselis.tpmsadvanced.qrcode"
    buildFeatures.compose = true
    composeOptions {
        val composeCompilerVersion: String by project
        kotlinCompilerExtensionVersion = composeCompilerVersion
    }
}

dependencies {
    api(project(":feature:core"))

    api(project(":core:common"))
    api(project(":core:ui"))

    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:18.2.0")
    implementation("androidx.camera:camera-camera2:1.2.2")
    implementation("androidx.camera:camera-lifecycle:1.2.2")
    implementation("androidx.camera:camera-mlkit-vision:1.3.0-alpha06")
}