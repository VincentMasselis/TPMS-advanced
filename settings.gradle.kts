import org.gradle.api.initialization.resolve.RepositoriesMode.FAIL_ON_PROJECT_REPOS

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

plugins {
    // From https://docs.gradle.org/current/userguide/platforms.html: "You cannot use a plugin
    // declared in a version catalog in your settings file or settings plugin (because catalogs are
    // defined in settings themselves, it would be a chicken and egg problem)."
    id("com.gradle.enterprise") version "3.13.3"
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode = FAIL_ON_PROJECT_REPOS
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

//rootProject.name = "TPMS Advanced" <-- Avoid to set this to keep module naming clean into the IDE, learn more: https://stackoverflow.com/questions/62762955/module-names-in-android-studio#comment136480349_62762955
include(":core:common")
include(":core:test")
include(":core:android-test")
include(":core:ui")
include(":core:debug-ui")
include(":core:database")
include(":data:unit")
include(":data:app")
include(":data:vehicle")
include(":feature:core")
include(":feature:unlocated")
include(":feature:qrcode")
include(":feature:background")
include(":feature:shortcut")
include(":feature:unit")
include(":app:phone")
