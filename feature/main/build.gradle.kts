plugins {
    `android-lib`
    compose
    dagger
    paparazzi
}

android {
    namespace = "com.masselis.tpmsadvanced.feature.main"
    buildFeatures   {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":data:vehicle"))
    implementation(project(":data:unit"))
    implementation(project(":data:app"))

    debugImplementation(project(":core:debug-ui"))
    debugImplementation(project(":core:android-test"))

    testImplementation(project(":core:test"))
}