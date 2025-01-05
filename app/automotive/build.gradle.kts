@file:Suppress("LocalVariableName", "UnstableApiUsage")

import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsPlugin
import com.google.gms.googleservices.GoogleServicesPlugin
import com.masselis.tpmsadvanced.emulator.EmulatorPlugin
import com.masselis.tpmsadvanced.gitflow.GitflowExtension
import com.masselis.tpmsadvanced.playstore.PlayStoreExtension
import com.masselis.tpmsadvanced.playstore.PlayStorePlugin
import com.masselis.tpmsadvanced.playstore.task.UpdatePlayStoreScreenshots

plugins {
    `android-app`
    compose
    dagger
    paparazzi
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
        applicationId = "us.berkovitz.aaos.tpmsadvanced"
        namespace = "com.masselis.tpmsadvanced"
        minSdk = 29
    }
    if (keys != null) {
        signingConfigs.create("release") {
            keyAlias = keys.appKeyAlias
            keyPassword = keys.appKeyStorePwd
            storeFile = file("../../secrets/app-keystore")
            storePassword = keys.appKeyStorePwd
        }
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.findByName("release") ?: signingConfigs["debug"]
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:ui-automotive"))
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

val clearTestOutputFilesFolder by tasks.creating(ClearTestOutputFilesFolder::class) {
    if (rootProject.plugins.hasPlugin(EmulatorPlugin::class))
        dependsOn(":waitForEmulator")
    adbExecutable = android.adbExecutable
}

val downloadTestOutputFiles by tasks.creating(DownloadTestOutputFiles::class) {
    dependsOn("connectedDemoDebugAndroidTest")
    adbExecutable = android.adbExecutable
    destination = layout.buildDirectory.dir("test_outputfiles")
}

val copyScreenshot by tasks.creating(Copy::class) {
    dependsOn(downloadTestOutputFiles)
    group = "publishing"
    description =
        "Copy and rename the screenshots from the phone in order to be uploaded to the play store listing"
    from(downloadTestOutputFiles.destination)
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

tasks.matching { it.name == "connectedDemoDebugAndroidTest" }.configureEach {
    dependsOn(clearTestOutputFilesFolder)
}
