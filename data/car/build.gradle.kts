plugins {
    id("android-lib")
    id("app.cash.sqldelight")
    id("dagger")
}

android {
    namespace = "com.masselis.tpmsadvanced.data.car"
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
    api(project(":core:common"))
    api(project(":core:database"))
    api(project(":data:record"))

    androidTestImplementation(project(":core:android-test"))
    androidTestUtil("androidx.test:orchestrator:$testServicesVersion")
}

sqldelight {
    databases {
        create("Database") {
            val sqlDelightVersion: String by project
            packageName.set("com.masselis.tpmsadvanced.data.car")
            dialect("app.cash.sqldelight:sqlite-3-24-dialect:$sqlDelightVersion")
            verifyMigrations.set(true)
            schemaOutputDirectory.set(file("src/main/sqldelight"))
        }
    }
}