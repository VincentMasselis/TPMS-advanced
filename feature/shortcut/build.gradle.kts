plugins {
    `android-lib`
    dagger
}

android {
    namespace = "com.masselis.tpmsadvanced.feature.shortcut"
}

dependencies {
    implementation(project(":feature:main"))

    implementation(project(":data:vehicle"))
    implementation(project(":data:unit"))
    implementation(project(":data:app"))

    implementation(project(":core:common"))
}