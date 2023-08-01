@file:Suppress("LocalVariableName", "UnstableApiUsage")

import com.masselis.tpmsadvanced.publisher.AndroidPublisherExtension
import com.masselis.tpmsadvanced.publisher.AndroidPublisherPlugin
import com.masselis.tpmsadvanced.publisher.PromoteToMain

plugins {
    id("android-app")
    id("dagger")
}

val isDecrypted: Boolean by rootProject.extra
if (isDecrypted) {
    // Needs the google-services.json file to work
    apply(plugin = "com.google.gms.google-services")
    apply(plugin = "com.google.firebase.crashlytics")
}

val tpmsAdvancedVersionCode: Int by rootProject.extra
android {
    defaultConfig {
        applicationId = "com.masselis.tpmsadvanced"
        namespace = "com.masselis.tpmsadvanced"

        versionCode = tpmsAdvancedVersionCode
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // `useTestStorageService` enables the ability to store files when capturing screenshots.
        // `clearPackageData` makes the Android Test Orchestrator run its "pm clear" command after
        // each test invocation. This command ensures that the app's state is completely cleared
        // between tests.
        testInstrumentationRunnerArguments += listOf(
            "useTestStorageService" to "true",
            "clearPackageData" to "true"
        )
    }
    testOptions.execution = "ANDROIDX_TEST_ORCHESTRATOR"
    signingConfigs {
        if (isDecrypted) create("release") {
            val APP_KEY_ALIAS: String by rootProject.extra
            val APP_KEY_STORE_PWD: String by rootProject.extra
            val APP_KEYSTORE_LOCATION: String by rootProject.extra
            keyAlias = APP_KEY_ALIAS
            keyPassword = APP_KEY_STORE_PWD
            storeFile = file(APP_KEYSTORE_LOCATION)
            storePassword = APP_KEY_STORE_PWD
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
    enableCompose(this)
}

dependencies {
    val testServicesVersion: String by project
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:debug-ui"))

    implementation(project(":data:app"))
    implementation(project(":data:record"))
    implementation(project(":data:unit"))
    implementation(project(":data:vehicle"))

    implementation(project(":feature:core"))
    implementation(project(":feature:unit"))
    implementation(project(":feature:qrcode"))
    implementation(project(":feature:background"))
    implementation(project(":feature:shortcut"))

    testImplementation(project(":core:test"))
    androidTestUtil("androidx.test:orchestrator:$testServicesVersion")
    androidTestUtil("androidx.test.services:test-services:$testServicesVersion")
    androidTestImplementation(project(":core:android-test"))
}

afterEvaluate {
    val clearTestOutputFilesFolder by tasks.creating(ClearTestOutputFilesFolder::class) {
        dependsOn(":waitForDevice")
        adbExecutable = android.adbExecutable
    }
    val connectedDemoDebugAndroidTest = tasks.getByPath("connectedDemoDebugAndroidTest")
    connectedDemoDebugAndroidTest.dependsOn(clearTestOutputFilesFolder)
    val outputFilesDir = layout.buildDirectory.dir("test_outputfiles")
    val downloadTestOutputFiles by tasks.creating(DownloadTestOutputFiles::class) {
        dependsOn(connectedDemoDebugAndroidTest)
        adbExecutable = android.adbExecutable
        destination = outputFilesDir
    }

    tasks.create<Copy>("copyScreenshot") {
        dependsOn(downloadTestOutputFiles)
        group = "publishing"
        description =
            "Copy and rename the screenshots from the phone in order to be uploaded to the play store listing"
        val path = "$projectDir/src/normal/play/listings/en-US/graphics/phone-screenshots"
        from(outputFilesDir)
        into(path)
        eachFile {
            when {
                name.startsWith("light_main") -> name = "1.png"
                name.startsWith("light_settings") -> name = "2.png"
                name.startsWith("dark_main") -> name = "3.png"
                name.startsWith("dark_settings") -> name = "4.png"
                else -> exclude()
            }
        }
    }
}

if (isDecrypted) {
    apply<AndroidPublisherPlugin>()
    configure<AndroidPublisherExtension> {
        serviceAccountCredentials = file("../../secrets/publisher-service-account.json")
    }
    tasks.withType<PromoteToMain> {
        dependsOn("copyScreenshot")
    }
}
