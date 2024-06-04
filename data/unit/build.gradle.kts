plugins {
    `android-lib`
    dagger
}

android {
    namespace = "com.masselis.tpmsadvanced.data.unit"
}

dependencies {
    implementation(project(":core:common"))
}