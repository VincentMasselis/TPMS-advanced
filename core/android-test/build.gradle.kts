plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.masselis.tpmsadvanced.core.androidtest"
}

dependencies {
    val coroutineVersion: String by project
    val kotlinVersion: String by project
    api(project(":core:common"))
    api("androidx.test.ext:junit:1.1.5")
    api("androidx.test:core-ktx:1.5.0")
    api("androidx.test.espresso:espresso-core:3.5.1")
    api("androidx.test:runner:1.5.2")
    api("androidx.test:rules:1.5.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutineVersion")
    api("io.mockk:mockk-android:1.13.4")
    api("androidx.compose.ui:ui-test-junit4")
    api("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    api("org.jetbrains.kotlin:kotlin-test")
}