plugins {
    id("android-lib")
    id("dagger")
}

android {
    namespace = "com.masselis.tpmsadvanced.feature.shortcut"
}

dependencies {
    implementation(project(":feature:core"))

    implementation(project(":data:vehicle"))
    implementation(project(":data:record"))
    implementation(project(":data:unit"))
    implementation(project(":data:app"))

    implementation(project(":core:common"))
}