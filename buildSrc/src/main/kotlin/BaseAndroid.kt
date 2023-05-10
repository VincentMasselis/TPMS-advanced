import com.android.build.gradle.BaseExtension
import gradle.kotlin.dsl.accessors._404981569cb7bc4f1f0ba7441ad57f27.lintChecks
import org.gradle.api.Action
import org.gradle.api.JavaVersion.VERSION_17
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.base(android: BaseExtension) =
    with(android) {
        compileSdkVersion(33)
        defaultConfig {
            minSdk = 27
            targetSdk = 33

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        compileOptions {
            sourceCompatibility = VERSION_17
            targetCompatibility = VERSION_17
        }
        flavorDimensions("mode")
        productFlavors {
            create("demo") {
                dimension = "mode"
            }
            create("normal") {
                dimension = "mode"
            }
        }
        variantFilter = Action {
            if (flavors.map { it.name }.contains("demo") && buildType.name == "release")
                ignore = true
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
        .also {
            afterEvaluate {
                if (android.buildFeatures.compose ?: false) {
                    dependencies {
                        lintChecks("com.slack.lint.compose:compose-lint-checks:1.2.0")
                    }
                }
            }
        }
        .also {
            tasks.withType<KotlinCompile>().all {
                kotlinOptions {
                    jvmTarget = VERSION_17.toString()
                    freeCompilerArgs += listOf(
                        "-Xexplicit-api=strict",
                        "-opt-in=kotlin.RequiresOptIn"
                    )
                }
            }
        }
