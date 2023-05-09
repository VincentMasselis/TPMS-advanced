plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.masselis.tpmsadvanced.core.test"
}

dependencies {
    val coroutineVersion: String by project
    api("junit:junit:4.13.2")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutineVersion")
    api("androidx.arch.core:core-testing:2.2.0")
    api("io.mockk:mockk:1.13.4")
    api("org.jetbrains.kotlin:kotlin-test")
}