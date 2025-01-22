plugins {
    `android-lib`
}

android {
    namespace = "com.masselis.tpmsadvanced.core.ui_automotive"
}

dependencies {
    api(project(":core:common"))
    api(libs.androidx.core)
    api(libs.androidx.car.app.app)
    api(libs.androidx.car.app.appautomotive)
    api(libs.androidx.constraintlayout)
}