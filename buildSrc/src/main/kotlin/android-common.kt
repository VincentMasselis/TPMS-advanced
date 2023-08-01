import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.tasks.DeviceProviderInstrumentTestTask
import org.gradle.api.Action
import org.gradle.api.JavaVersion.VERSION_17
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.existing
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.gradle.kotlin.dsl.maybeCreate
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.registering
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.base(android: BaseExtension) {
    with(android) {
        compileSdkVersion(33)
        defaultConfig {
            minSdk = 27
            targetSdk = 33
            buildToolsVersion("33.0.2")

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        compileOptions {
            sourceCompatibility = VERSION_17
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
    }
    tasks.withType<KotlinCompile>().all {
        kotlinOptions {
            jvmTarget = VERSION_17.toString()
            freeCompilerArgs += listOf(
                "-Xexplicit-api=strict",
                "-opt-in=kotlin.RequiresOptIn",
                "-Xcontext-receivers"
            )
        }
    }

    val waitForDevice = rootProject
        .tasks
        .maybeCreate<WaitForDeviceToBeReadyOnCiMachine>("waitForDevice")
        .apply { adbExecutable = android.adbExecutable }
    tasks.withType<DeviceProviderInstrumentTestTask>().all {
        dependsOn(waitForDevice)
    }
}

public fun Project.enableCompose(android: BaseExtension) {
    with(android) {
        buildFeatures.compose = true
        composeOptions {
            val composeCompilerVersion: String by project
            kotlinCompilerExtensionVersion = composeCompilerVersion
        }
    }
    dependencies {
        add("lintChecks", "com.slack.lint.compose:compose-lint-checks:1.2.0")
    }
}