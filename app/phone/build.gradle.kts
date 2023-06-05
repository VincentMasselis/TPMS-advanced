@file:Suppress("LocalVariableName")

import com.github.triplet.gradle.androidpublisher.ResolutionStrategy.AUTO
import com.github.triplet.gradle.play.PlayPublisherExtension

plugins {
    id("android-app")
    id("dagger")
}

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
        versionName = "1.1.1"

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
    implementation(project(":feature:core"))
    implementation(project(":feature:unit"))
    implementation(project(":feature:qrcode"))
    implementation(project(":feature:background"))

    testImplementation(project(":core:test"))
    androidTestUtil("androidx.test:orchestrator:$testServicesVersion")
    androidTestUtil("androidx.test.services:test-services:$testServicesVersion")
    androidTestImplementation(project(":core:android-test"))
}

tasks.whenTaskAdded {
    if (name == "connectedDemoDebugAndroidTest") {
        val connectedDemoDebugAndroidTest = this
        task<ClearTestOutputFilesFolder>("clearTestOutputFilesFolder") {
            connectedDemoDebugAndroidTest.dependsOn(this)
            adbExecutable.set(android.adbExecutable)
        }

        val outputFilesDir = layout.buildDirectory.dir("test_outputfiles")
        val downloadTestOutputFiles = task<DownloadTestOutputFiles>("downloadTestOutputFiles") {
            dependsOn(connectedDemoDebugAndroidTest)
            adbExecutable.set(android.adbExecutable)
            destination.set(outputFilesDir)
        }
        task<Copy>("copyScreenshot") {
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
}

if (isDecrypted) afterEvaluate {
    tasks.filter { it.name.startsWith("generate") && it.name.endsWith("PlayResources") }
        .forEach { generatePlayResources ->
            // generatePlayResources is a task used by "publishListing" which watch at the
            // "phone-screenshots" folder in order to check if changes was made. The task
            // "generatePlayResources" must be launched after "copyScreenshot" otherwise no
            // screenshot will be uploaded at all.
            generatePlayResources.mustRunAfter("copyScreenshot")
        }

    // Play store listing must depends on the task which generates screenshots
    tasks.filter { it.name.startsWith("publish") && it.name.endsWith("Listing") }
        .forEach { publishListing ->
            publishListing.dependsOn("copyScreenshot")
        }

    // Removes dependency which updates the play store listing when publishing a new app in beta
    // because the play store listing reflects the app in production, not the beta.
    tasks.filter { it.name.startsWith("publish") && it.name.endsWith("Apps") }
        .forEach { publishApps ->
            publishApps.dependsOn.removeIf {
                // Unlike Kotlin, Groovy is able to get the task name of the current dependency even
                // if the dependency is nested into the gradle class "Provider". On its side, with
                // Kotlin, you have to unwrap the task before asking for its name which is not as
                // simple as Groovy so I prefer to use "withGroovyBuilder" in this case.
                val taskName = it.withGroovyBuilder { "getName"() } as String
                taskName.startsWith("publish") && taskName.endsWith("Listing")
            }
        }

    // Create the task compareLocalVersionCodeWithPlayStore
    tasks
        .single { it.name == "processNormalReleaseVersionCodes" }
        .let { processNormalReleaseVersionCodes ->
            task<CompareLocalVersionCodeWithPlayStore>("compareLocalVersionCodeWithPlayStore") {
                dependsOn(processNormalReleaseVersionCodes)
                availableVersionCodeFile.set(file("$buildDir/intermediates/gpp/normalRelease/available-version-codes.txt"))
                currentVc.set(tpmsAdvancedVersionCode)
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
