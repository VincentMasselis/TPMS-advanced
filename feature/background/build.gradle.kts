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
    api(project(":data:car"))
    api(project(":data:unit"))
    api(project(":feature:core"))

    api(project(":core:debug-ui"))
}