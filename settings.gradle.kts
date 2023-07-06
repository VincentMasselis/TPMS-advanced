import org.gradle.api.initialization.resolve.RepositoriesMode.FAIL_ON_PROJECT_REPOS

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    val agpVersion: String by settings
    val kotlinVersion: String by settings
    val sqlDelightVersion: String by settings
    plugins {
        id("com.android.application") version agpVersion
        id("com.android.library") version agpVersion
        id("org.jetbrains.kotlin.android") version kotlinVersion
        id("org.jetbrains.kotlin.kapt") version kotlinVersion
        id("com.google.gms.google-services") version "4.3.15"
        id("com.google.firebase.crashlytics") version "2.9.6"
        id("com.github.triplet.play") version "3.8.3"
        id("app.cash.sqldelight") version sqlDelightVersion
        id("io.gitlab.arturbosch.detekt") version "1.23.0"
        id("dev.shreyaspatil.compose-compiler-report-generator") version "1.0.0"
    }
}

plugins {
    id("com.gradle.enterprise") version "3.13.3"
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

rootProject.name = "TPMS Advanced"
include(":core:common")
include(":core:test")
include(":core:android-test")
include(":core:ui")
include(":core:debug-ui")
include(":core:database")
include(":data:record")
include(":data:unit")
include(":data:app")
include(":data:car")
include(":feature:core")
include(":feature:unit")
include(":feature:qrcode")
include(":feature:background")
include(":feature:shortcut")
include(":app:phone")
