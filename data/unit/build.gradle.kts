plugins {
    id("android-lib")
    id("dagger")
}

android {
    namespace = "com.masselis.tpmsadvanced.data.unit"
}

dependencies {
    api(project(":core:common"))
}