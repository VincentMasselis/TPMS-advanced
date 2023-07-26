plugins {
    id("android-lib")
    id("dagger")
}

android {
    namespace = "com.masselis.tpmsadvanced.feature.background"
    enableCompose(this)
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:common"))

    implementation(project(":data:vehicle"))
    implementation(project(":data:unit"))
    implementation(project(":data:record"))
    implementation(project(":data:app"))

    implementation(project(":feature:core"))

    debugImplementation(project(":core:debug-ui"))
}