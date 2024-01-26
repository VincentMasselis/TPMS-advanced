@file:Suppress("LocalVariableName", "UnstableApiUsage")

import com.masselis.tpmsadvanced.github.GithubExtension
import com.masselis.tpmsadvanced.github.GithubPlugin
import com.masselis.tpmsadvanced.playstore.PlayStoreExtension
import com.masselis.tpmsadvanced.playstore.PlayStorePlugin
import com.masselis.tpmsadvanced.playstore.UpdatePlayStoreScreenshots

plugins {
    `android-app`
    compose
    dagger
    paparazzi
}

val isDecrypted: Boolean by rootProject.extra
if (isDecrypted) {
    // Needs the google-services.json file to work
    apply(plugin = libs.plugins.google.services.get().pluginId)
    apply(plugin = libs.plugins.crashlytics.get().pluginId)

    apply<PlayStorePlugin>()
    configure<PlayStoreExtension> {
        serviceAccountCredentials = file("../../secrets/publisher-service-account.json")
    }
    apply<GithubPlugin>()
    configure<GithubExtension> {
        val GITHUB_TOKEN: String by rootProject.extra
        githubToken = GITHUB_TOKEN
    }
}

val tpmsAdvancedVersionCode: Int by rootProject.extra
val tpmsAdvancedVersionName: String by rootProject.extra
android {
    defaultConfig {
        applicationId = "com.masselis.tpmsadvanced"
        namespace = "com.masselis.tpmsadvanced"

        versionCode = tpmsAdvancedVersionCode
        versionName = tpmsAdvancedVersionName

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
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:debug-ui"))

    implementation(project(":data:app"))
    implementation(project(":data:unit"))
    implementation(project(":data:vehicle"))

    implementation(project(":feature:background"))
    implementation(project(":feature:core"))
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
    dependsOn(":waitForDevice")
    adbExecutable = android.adbExecutable
}

val downloadTestOutputFiles by tasks.creating(DownloadTestOutputFiles::class) {
    adbExecutable = android.adbExecutable
    destination = layout.buildDirectory.dir("test_outputfiles")
}

val copyScreenshot by tasks.creating(Copy::class) {
    dependsOn(downloadTestOutputFiles)
    group = "publishing"
    description =
        "Copy and rename the screenshots from the phone in order to be uploaded to the play store listing"
    val path = "$projectDir/src/normal/play/listings/en-US/graphics/phone-screenshots"
    from(downloadTestOutputFiles.destination)
    into(path)
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
    downloadTestOutputFiles.dependsOn(this)
}
