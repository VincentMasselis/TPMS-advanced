@file:Suppress("UnstableApiUsage")

plugins {
    `android-lib`
    alias(libs.plugins.sqldelight)
    dagger
}

android {
    namespace = "com.masselis.tpmsadvanced.data.vehicle"
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
