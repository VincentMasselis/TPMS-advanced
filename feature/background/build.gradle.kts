plugins {
    id("android-lib")
    id("dagger")
}

android {
    namespace = "com.masselis.tpmsadvanced.core.feature.background"
    enableCompose(this)
}

dependencies {
    api(project(":core:ui"))
    api(project(":core:common"))
    api(project(":core:debug-ui"))
    api(project(":data:car"))
}