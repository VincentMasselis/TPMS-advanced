plugins {
    id("android-lib")
    id("dagger")
}

android {
    namespace = "com.masselis.tpmsadvanced.data.record"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":data:unit"))
}