@file:Suppress("UnstableApiUsage")

plugins {
    id("android-lib")
    id("app.cash.sqldelight")
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
    val testServicesVersion: String by project
    implementation(project(":core:common"))
    implementation(project(":core:database"))
    implementation(project(":data:record"))

    androidTestImplementation(project(":core:android-test"))
    androidTestUtil("androidx.test:orchestrator:$testServicesVersion")
}

sqldelight {
    databases {
        create("Database") {
            val sqlDelightVersion: String by project
            packageName = "com.masselis.tpmsadvanced.data.vehicle"
            dialect("app.cash.sqldelight:sqlite-3-38-dialect:$sqlDelightVersion")
            verifyMigrations = true
            schemaOutputDirectory = file("src/main/sqldelight")
        }
    }
}