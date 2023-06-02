plugins {
    id("android-lib")
    id("dagger")
}

android {
    namespace = "com.masselis.tpmsadvanced.core"
    enableCompose(this)
}

dependencies {
    api(project(":core:common"))
    api(project(":core:ui"))
    api(project(":data:record"))
    api(project(":data:car"))
    api(project(":feature:unit"))
    api(project(":feature:background"))

    implementation(project(":core:debug-ui"))

    testImplementation(project(":core:test"))
}