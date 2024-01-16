plugins {
    id("android-lib")
    id("dagger")
}

android {
    namespace = "com.masselis.tpmsadvanced.data.unit"
}

dependencies {
    implementation(project(":core:common"))
}