plugins {
    id("android-lib")
    id("dagger")
}

android {
    namespace = "com.masselis.tpmsadvanced.feature.shortcut"
}

dependencies {
    api(project(":feature:core"))
}