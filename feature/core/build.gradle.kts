plugins {
    id("android-lib")
    id("dagger")
}

@Suppress("UnstableApiUsage")
android {
    namespace = "com.masselis.tpmsadvanced.core"
    buildFeatures.compose = true
    composeOptions {
        val composeCompilerVersion: String by project
        kotlinCompilerExtensionVersion = composeCompilerVersion
    }
}

dependencies {
    api(project(":core:common"))
    api(project(":core:ui"))
    api(project(":data:record"))
    api(project(":data:car"))
    api(project(":feature:unit"))

    implementation(project(":core:debug-ui"))

    testImplementation(project(":core:test"))
}