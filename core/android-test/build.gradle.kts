plugins {
    `android-lib`
}

android {
    namespace = "com.masselis.tpmsadvanced.core.androidtest"
}

dependencies {
    api(project(":core:common"))
    api(libs.androidx.test.junit)
    api(libs.androidx.test.core)
    api(libs.androidx.test.espresso.core)
    api(libs.androidx.test.espresso.device)
    api(libs.androidx.test.runner)
    api(libs.androidx.test.rules)
    api(libs.androidx.compose.junit)
    api(libs.kotlinx.coroutines.test)
    api(libs.turbine)
    api(libs.mockk.android)
    api(libs.kotlin.reflect)
    api(libs.kotlin.test)

}