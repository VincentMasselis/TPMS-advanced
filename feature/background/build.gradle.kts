plugins {
    `android-lib`
    compose
    dagger
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

    implementation(project(":feature:core"))

    debugImplementation(project(":core:debug-ui"))
}