@file:Suppress("LocalVariableName")

import com.github.triplet.gradle.androidpublisher.ResolutionStrategy.AUTO
import com.github.triplet.gradle.play.PlayPublisherExtension
import groovy.lang.GString

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
apply(from = "${project.rootDir}/gradle/dagger.gradle")

val isDecrypted: Boolean by rootProject.extra
if (isDecrypted) {
    // Needs the google-services.json file to work
    apply(plugin = "com.google.gms.google-services")
    apply(plugin = "com.google.firebase.crashlytics")

    // Needs the publisher-service-account.json file to work
    apply(plugin = "com.github.triplet.play")
    configure<PlayPublisherExtension> {
        serviceAccountCredentials.set(file("../../secrets/publisher-service-account.json"))
        defaultToAppBundles.set(true)
        track.set("beta")
        // Keep the strategy AUTO to continue generating "available-version-codes.txt" files
        resolutionStrategy.set(AUTO)
        fromTrack.set("beta")
        promoteTrack.set("production")
    }
}
val tpmsAdvancedVersionCode: Int by rootProject.extra
@Suppress("UnstableApiUsage")
android {
    defaultConfig {
        applicationId = "com.masselis.tpmsadvanced"
        namespace = "com.masselis.tpmsadvanced"

        versionCode = tpmsAdvancedVersionCode
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // `useTestStorageService` enables the ability to store files when capturing screenshots.
        // `clearPackageData` makes the Android Test Orchestrator run its "pm clear" command after
        // each test invocation. This command ensures that the app's state is completely cleared
        // between tests.
        testInstrumentationRunnerArguments += "useTestStorageService" to "true"
        testInstrumentationRunnerArguments += "clearPackageData" to "true"
        testOptions.execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }
    signingConfigs {
        if (isDecrypted)
            create("release") {
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
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        val composeCompilerVersion: String by project
        kotlinCompilerExtensionVersion = composeCompilerVersion
    }
}

dependencies {
    val testServicesVersion: String by project
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:debug-ui"))
    implementation(project(":data:app"))
    implementation(project(":feature:core"))
    implementation(project(":feature:qrcode"))

    testImplementation(project(":core:test"))
    androidTestUtil("androidx.test:orchestrator:$testServicesVersion")
    androidTestUtil("androidx.test.services:test-services:$testServicesVersion")
    androidTestImplementation(project(":core:android-test"))
}

lateinit var copyScreenshot: Task
tasks.whenTaskAdded {
    if (name == "connectedDemoDebugAndroidTest") {
        val connectedDemoDebugAndroidTest = this
        task<Exec>("clearTestOutputFilesFolder") {
            connectedDemoDebugAndroidTest.dependsOn(this)
            description = "Clears the phone\'s screenshot folder"
            commandLine(
                android.adbExecutable,
                "shell",
                "rm -rf /sdcard/googletest/test_outputfiles"
            )
        }
        val downloadTestOutputFiles = task<Exec>("downloadTestOutputFiles") {
            dependsOn(connectedDemoDebugAndroidTest)
            description = "Download screenshot folder from the phone"
            doFirst {
                delete(layout.buildDirectory.dir("test_outputfiles"))
                mkdir(layout.buildDirectory.dir("test_outputfiles"))
            }
            commandLine(
                android.adbExecutable,
                "pull",
                "/sdcard/googletest/test_outputfiles",
                buildDir
            )
        }
        copyScreenshot = task<Copy>("copyScreenshot") {
            dependsOn(downloadTestOutputFiles)
            group = "publishing"
            description =
                "Copy and rename the screenshots from the phone in order to be uploaded to the play store listing"
            val path = "$projectDir/src/normal/play/listings/en-US/graphics/phone-screenshots"
            doFirst { mkdir(path) }
            from(layout.buildDirectory.dir("test_outputfiles"))
            into(path)
            rename { filename ->
                if (filename.startsWith("light_main"))
                    "1.png"
                else if (filename.startsWith("light_settings"))
                    "2.png"
                else if (filename.startsWith("dark_main"))
                    "3.png"
                else if (filename.startsWith("dark_settings"))
                    "4.png"
                else
                    filename
            }
        }
    }
}

if (isDecrypted) afterEvaluate {
    // Play store listing must depends on the task which generates screenshots
    tasks.filter { it.name.startsWith("publish") && it.name.endsWith("Listing") }
        .forEach { publishListing ->
            publishListing.dependsOn(copyScreenshot)
        }

    // Removes dependency which updates the play store listing when publishing a new app in beta
    // because the play store listing reflects the app in production, not the beta.
    tasks.filter { it.name.startsWith("publish") && it.name.endsWith("Apps") }
        .forEach { task ->
            task.setDependsOn(task.dependsOn.filter {
                (name.startsWith("publish") && name.endsWith("Listing")).not()
            })
        }

    // Create the task compareLocalVersionCodeWithPlayStore
    tasks
        .single { it.name == "processNormalReleaseVersionCodes" }
        .let { processNormalReleaseVersionCodes ->
            task("compareLocalVersionCodeWithPlayStore") {
                dependsOn(processNormalReleaseVersionCodes)
                group = "publishing"
                description =
                    "Ensure the artifact to be promoted by promoteArtifact will be generated from the current commit"
                doLast {
                    val playStoreVc =
                        file("$buildDir/intermediates/gpp/normalRelease/available-version-codes.txt")
                            .readText()
                            .trim()
                            .toInt() - 1
                    assert(playStoreVc == tpmsAdvancedVersionCode)
                }
            }
        }
        .also { compareLocalVersionCodeWithPlayStore ->
            // When promoting beta to production, I ensure the current commit is the one which was sent to the
            // play store into by adding a dependency to the task compareLocalVersionCodeWithPlayStore
            tasks
                .filter { it.name.startsWith("promote") && it.name.endsWith("Artifact") }
                .forEach { it.dependsOn(compareLocalVersionCodeWithPlayStore) }
        }
}
