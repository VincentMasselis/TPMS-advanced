@file:Suppress("UnstableApiUsage")

plugins {
    `android-lib`
    `android-test`
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.metro)
}

android {
    namespace = "com.masselis.tpmsadvanced.data.vehicle"
}

dependencies {
    implementation(project(":core:common"))
    api(project(":core:database"))
    implementation(project(":data:unit"))

    testImplementation(project(":core:test"))

    androidTestImplementation(libs.requery)
}

sqldelight {
    databases {
        create("Database") {
            packageName = "com.masselis.tpmsadvanced.data.vehicle"
            dialect(libs.sqldelight.dialect338)
            verifyMigrations = true
            schemaOutputDirectory = file("src/main/sqldelight")

            val migrations = schemaOutputDirectory
                .dir("migrations")
                .get()
                .asFileTree
                .matching { include("*.sqm") }
                .files
                .sortedBy { it.nameWithoutExtension }
                .map { it.nameWithoutExtension }
            val snapshots = schemaOutputDirectory
                .get()
                .asFileTree
                .matching { include("*.db") }
                .files
                .sortedBy { it.nameWithoutExtension }
                .map { it.nameWithoutExtension }
            // Check that all migrations have a corresponding snapshot
            for (migration in migrations) {
                if (snapshots.none { it == migration })
                    throw GradleException("A base snapshot named \"$migration.db\" is missing to run migration \"$migration.sqm\"")
            }
            // Check a snapshot exists after the latest migration
            if (snapshots.last() == migrations.last()) {
                throw GradleException("The latest migration exists but no snapshot is available, run \"generateNormalDebugDatabaseSchema\"")
            }
        }
    }
}
