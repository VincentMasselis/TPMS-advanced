plugins {
    `android-lib`
    `android-test`
    alias(libs.plugins.metro)
}

android {
    namespace = "com.masselis.tpmsadvanced.feature.androidauto"
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":data:vehicle"))
    implementation(project(":data:unit"))
    implementation(project(":data:app"))
    implementation(project(":feature:main"))
    implementation(libs.androidx.car.app)

    androidTestImplementation(libs.androidx.car.app.testing)
}