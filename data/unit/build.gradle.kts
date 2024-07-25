plugins {
    `android-lib`
    koin
}

android {
    namespace = "com.masselis.tpmsadvanced.data.unit"
}

dependencies {
    implementation(project(":core:common"))
}