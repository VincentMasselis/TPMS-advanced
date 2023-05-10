plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger")
}

@Suppress("UnstableApiUsage")
android {
    namespace = "com.masselis.tpmsadvanced.core.common"
    buildFeatures.buildConfig = true
}

dependencies {
    val lifecycleVersion: String by project
    val coroutineVersion: String by project
    api("androidx.core:core-ktx:1.10.0")
    // For an unknown reason, startup-runtime tries to load DefaultLifecycleObserver when running an
    // instrumented test. To avoid crashes in this case, I manually add this dependency at the same
    // place than startup-runtime
    implementation("androidx.lifecycle:lifecycle-common:$lifecycleVersion")
    api("androidx.startup:startup-runtime:1.1.1")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$coroutineVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-guava:$coroutineVersion")
    api("com.squareup.okio:okio:3.3.0")
    api("com.google.firebase:firebase-crashlytics-ktx:18.3.7")
    api(platform("androidx.compose:compose-bom:2023.04.01"))
}