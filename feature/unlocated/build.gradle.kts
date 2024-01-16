plugins {
    id("android-lib")
    id("compose")
    id("dagger")
}

android {
    namespace = "com.masselis.tpmsadvanced.unlocated"
}

dependencies {
    implementation(project(":feature:core"))

    implementation(project(":data:vehicle"))
    implementation(project(":data:unit"))

    implementation(project(":core:common"))
    implementation(project(":core:ui"))

    debugImplementation(project(":core:android-test"))

    testImplementation(project(":core:test"))
}