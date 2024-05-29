import org.gradle.api.initialization.resolve.RepositoriesMode.FAIL_ON_PROJECT_REPOS

pluginManagement {
    includeBuild("gradle-included-build")
    repositories {
        gradlePluginPortal()
        google()
    }
}

plugins {
    id("settings-plugins")
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
include(":feature:main")
include(":feature:unlocated")
include(":feature:qrcode")
include(":feature:background")
include(":feature:shortcut")
include(":feature:unit")
include(":app:phone")
