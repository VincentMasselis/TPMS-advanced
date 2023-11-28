plugins {
    id("android-lib")
    id("compose")
    id("dagger")
}

android {
    namespace = "com.masselis.tpmsadvanced.core"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":data:vehicle"))
    implementation(project(":data:unit"))
    implementation(project(":data:app"))

    debugImplementation(project(":core:debug-ui"))

    testImplementation(project(":core:test"))
}