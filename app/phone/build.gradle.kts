@file:Suppress("LocalVariableName", "UnstableApiUsage")

import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsPlugin
import com.google.gms.googleservices.GoogleServicesPlugin
import com.masselis.tpmsadvanced.gitflow.GitflowExtension
import com.masselis.tpmsadvanced.playstore.PlayStoreExtension
import com.masselis.tpmsadvanced.playstore.PlayStorePlugin
import com.masselis.tpmsadvanced.playstore.task.UpdatePlayStoreScreenshots

plugins {
    `android-app`
    compose
    dagger
    alias(libs.plugins.paparazzi)
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.crashlytics) apply false
}

val keys = rootProject.extra.getOrNull<Keys>("keys")
if (keys != null) {
    // Needs the google-services.json file to work
    apply<GoogleServicesPlugin>()
    apply<CrashlyticsPlugin>()
    apply<PlayStorePlugin>()
    configure<PlayStoreExtension> {
        version = rootProject.the<GitflowExtension>().version
        serviceAccountCredentials = file("../../secrets/publisher-service-account.json")
    }
}

android {
    defaultConfig {
        applicationId = "com.masselis.tpmsadvanced"
        namespace = "com.masselis.tpmsadvanced"
    }
    if (keys != null) {
        signingConfigs.create("release") {
            keyAlias = keys.appKeyAlias
            keyPassword = keys.appKeyStorePwd
            storeFile = file("../../secrets/app-keystore")
            storePassword = keys.appKeyStorePwd
        }
    }
    buildTypes.getByName("release") {
        signingConfig = signingConfigs.findByName("release") ?: signingConfigs["debug"]
        isMinifyEnabled = true
        isShrinkResources = true
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
        )
    }
    buildFeatures.buildConfig = true
    val pixel2api34 by testOptions.managedDevices.localDevices.creating {
        device = "Pixel 2"
        apiLevel = 34
        systemImageSource = "aosp-atd"
    }
    val copyScreenshot by tasks.creating(Copy::class) {
        dependsOn("${pixel2api34.name}DemoDebugAndroidTest")
        group = "publishing"
        description =
            "Copy and rename the screenshots from the phone in order to be uploaded to the play store listing"
        from(layout.buildDirectory.dir("outputs/managed_device_android_test_additional_output/debug/flavors/demo/${pixel2api34.name}"))
        into("$projectDir/src/normal/play/listings/en-US/graphics/phone-screenshots")
        eachFile {
            name = when {
                name.startsWith("light_main") -> "1.png"
                name.startsWith("light_settings") -> "2.png"
                name.startsWith("light_binding_method") -> "3.png"
                name.startsWith("dark_main") -> "4.png"
                name.startsWith("dark_settings") -> "5.png"
                name.startsWith("dark_binding_method") -> "6.png"
                else -> throw GradleException("File with name $name not recognized")
            }
        }
    }
    tasks.withType<UpdatePlayStoreScreenshots> {
        dependsOn(copyScreenshot)
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:debug-ui"))

    implementation(project(":data:app"))
    implementation(project(":data:unit"))
    implementation(project(":data:vehicle"))

    implementation(project(":feature:background"))
    implementation(project(":feature:main"))
    implementation(project(":feature:unlocated"))
    implementation(project(":feature:qrcode"))
    implementation(project(":feature:shortcut"))
    implementation(project(":feature:unit"))

    testImplementation(project(":core:test"))

    androidTestUtil(libs.test.orchestrator)
    androidTestUtil(libs.test.services)
    androidTestImplementation(project(":core:android-test"))
}
