@file:Suppress("UnstableApiUsage")

plugins {
    id("android-lib")
    alias(libs.plugins.sqldelight)
    id("dagger")
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
    implementation(project(":core:database"))
    implementation(project(":data:unit"))

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