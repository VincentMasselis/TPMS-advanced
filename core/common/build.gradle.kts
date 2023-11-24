plugins {
    id("android-lib")
    id("dagger")
}

android {
    namespace = "com.masselis.tpmsadvanced.core.common"
    buildFeatures.buildConfig = true
}

dependencies {
    api(libs.androidx.core)
    // For an unknown reason, startup-runtime tries to load DefaultLifecycleObserver when running an
    // instrumented test. To avoid crashes in this case, I manually add this dependency at the same
    // place than startup-runtime
    implementation(libs.lifecycle.common)
    api(libs.androidx.startup)
    api(libs.kotlinx.coroutines.android)
    api(libs.kotlinx.coroutines.playservices)
    api(libs.kotlinx.coroutines.guava)
    api(libs.google.firebase)
    api(platform(libs.androidx.compose.bom))
}