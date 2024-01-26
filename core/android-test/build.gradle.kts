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
    api(libs.androidx.test.expresso)
    api(libs.androidx.test.runner)
    api(libs.androidx.test.rules)
    api(libs.kotlinx.coroutines.test)
    api(libs.mockk.android)
    api(libs.androidx.compose.junit)
    api(libs.kotlin.reflect)
    api(libs.kotlin.test)

}