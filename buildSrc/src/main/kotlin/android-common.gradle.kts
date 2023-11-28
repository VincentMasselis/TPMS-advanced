import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.tasks.DeviceProviderInstrumentTestTask
import org.gradle.api.JavaVersion.VERSION_17

plugins {
    com.android.base
    kotlin("android")
}

// `android {}` is unavailable since I only use the plugin com.android.base
the<BaseExtension>().apply android@{
    compileSdkVersion(34)
    defaultConfig {
        minSdk = 27
        targetSdk = 34
        buildToolsVersion("34.0.0")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        // Using sdk 34 allow the usage of Java 17 compatibility
        // Check https://developer.android.com/build/jdks#compileSdk
        targetCompatibility = VERSION_17
    }
    flavorDimensions("mode")
    productFlavors {
        val demo by creating {
            dimension = "mode"
        }
        create("normal") {
            dimension = "mode"
        }
        variantFilter = Action {
            if (flavors.any { it.name == demo.name } && buildType.name == "release")
                ignore = true
        }
    }
    packagingOptions {
        resources.excludes += setOf(
            "META-INF/DEPENDENCIES",
            "META-INF/LICENSE",
            "META-INF/LICENSE.txt",
            "META-INF/LICENSE.md",
            "META-INF/LICENSE-notice.md",
            "META-INF/license.txt",
            "META-INF/NOTICE",
            "META-INF/NOTICE.txt",
            "META-INF/notice.txt",
            "META-INF/ASL2.0",
            "META-INF/*.kotlin_module",
        )
    }

    val waitForDevice = rootProject
        .tasks
        .maybeCreate<WaitForDeviceToBeReadyOnCiMachine>("waitForDevice")
        .apply { adbExecutable = this@android.adbExecutable }
    tasks.withType<DeviceProviderInstrumentTestTask>().all {
        dependsOn(waitForDevice)
    }
}

// Does the same than `android.kotlinOptions {}`
kotlin {
    // See https://developer.android.com/build/jdks#toolchain
    jvmToolchain(17)
    compilerOptions {
        // No need to set `jvmTarget`, it uses `jvmToolchain` by default
        freeCompilerArgs.addAll(
            "-Xexplicit-api=strict",
            "-opt-in=kotlin.RequiresOptIn",
            "-Xcontext-receivers",
        )
    }
}