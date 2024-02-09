@file:Suppress("UnstableApiUsage")

import com.android.build.gradle.internal.tasks.DeviceProviderInstrumentTestTask
import com.android.build.gradle.tasks.MergeSourceSetFolders

plugins {
    `android-lib`
    alias(libs.plugins.sqldelight)
    dagger
}

android {
    namespace = "com.masselis.tpmsadvanced.data.vehicle"
    defaultConfig {
        // The following argument makes the Android Test Orchestrator run its
        // "pm clear" command after each test invocation. This command ensures
        // that the app's state is completely cleared between tests.
        testInstrumentationRunnerArguments += "clearPackageData" to "true"
    }
    testOptions.execution = "ANDROIDX_TEST_ORCHESTRATOR"
}

dependencies {
    implementation(project(":core:common"))
    api(project(":core:database"))
    implementation(project(":data:unit"))

    testImplementation(project(":core:test"))

    androidTestImplementation(project(":core:android-test"))
    androidTestUtil(libs.test.orchestrator)
}

sqldelight {
    databases {
        create("Database") {
            packageName = "com.masselis.tpmsadvanced.data.vehicle"
            dialect(libs.sqldelight.dialect338)
            verifyMigrations = true
            schemaOutputDirectory = file("src/main/sqldelight")
        }
    }
}
