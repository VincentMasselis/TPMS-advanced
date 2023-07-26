plugins {
    id("android-lib")
    id("dagger")
}

android {
    namespace = "com.masselis.tpmsadvanced.core"
    enableCompose(this)
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":data:record"))
    implementation(project(":data:vehicle"))
    implementation(project(":data:unit"))
    implementation(project(":data:app"))

    debugImplementation(project(":core:debug-ui"))

    testImplementation(project(":core:test"))
}