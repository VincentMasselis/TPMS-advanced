plugins {
    `android-lib`
    alias(libs.plugins.metro)
}

android {
    namespace = "com.masselis.tpmsadvanced.feature.androidauto"
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":data:vehicle"))
    implementation(project(":data:unit"))
    implementation(project(":feature:main"))
    implementation(libs.androidx.car.app)
}