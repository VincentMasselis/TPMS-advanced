plugins {
    `android-lib`
    compose
    dagger
    alias(libs.plugins.paparazzi)
}

android {
    namespace = "com.masselis.tpmsadvanced.feature.unlocated"
}

dependencies {
    implementation(project(":feature:main"))

    implementation(project(":data:vehicle"))
    implementation(project(":data:unit"))

    implementation(project(":core:common"))
    implementation(project(":core:ui"))

    debugImplementation(project(":core:android-test"))

    testImplementation(project(":core:test"))
}