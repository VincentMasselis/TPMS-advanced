plugins {
    id("android-lib")
    id("dagger")
}

android {
    namespace = "com.masselis.tpmsadvanced.data.record"
}

dependencies {
    api(project(":core:common"))
    api(project(":data:unit"))
}