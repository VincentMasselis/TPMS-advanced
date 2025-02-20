plugins {
    `android-lib`
    dagger
}

android {
    namespace = "com.masselis.tpmsadvanced.feature.androidauto"
}

dependencies {
    implementation(libs.androidx.car.app)
}