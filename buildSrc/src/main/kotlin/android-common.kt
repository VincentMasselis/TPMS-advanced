import com.android.build.gradle.BaseExtension
import org.gradle.api.Action
import org.gradle.api.JavaVersion.VERSION_17
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun Project.base(android: BaseExtension) {
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
        "demo".also { demoFlavorName ->
            productFlavors {
                create(demoFlavorName) {
                    dimension = "mode"
                }
                create("normal") {
                    dimension = "mode"
                }
            }
            variantFilter = Action {
                if (flavors.any { it.name == demoFlavorName } && buildType.name == "release")
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
                "-opt-in=kotlin.RequiresOptIn"
            )
        }
    }
    afterEvaluate {
        if (android.buildFeatures.compose ?: false) {
            dependencies {
                add("lintChecks", "com.slack.lint.compose:compose-lint-checks:1.2.0")
            }
        }
    }
}