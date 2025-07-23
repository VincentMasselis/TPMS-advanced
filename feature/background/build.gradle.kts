plugins {
    `android-lib`
    compose
    alias(libs.plugins.metro)
}

android {
    namespace = "com.masselis.tpmsadvanced.feature.background"
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:common"))

    implementation(project(":data:vehicle"))
    implementation(project(":data:unit"))
    implementation(project(":data:app"))

    implementation(project(":feature:main"))

    debugImplementation(project(":core:debug-ui"))

    testImplementation(project(":core:test"))
}