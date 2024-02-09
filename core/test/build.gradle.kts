plugins {
    `android-lib`
}

android {
    namespace = "com.masselis.tpmsadvanced.core.test"
}

dependencies {
    api(libs.junit)
    api(libs.kotlinx.coroutines.test)
    api(libs.androidx.arch.core.testing)
    api(libs.mockk.jvm)
    api(libs.kotlin.test)
    api(libs.kotlin.reflect)
    api(libs.turbine)

    implementation(project(":core:database"))
}