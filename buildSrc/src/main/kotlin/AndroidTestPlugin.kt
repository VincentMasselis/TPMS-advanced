import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

internal class AndroidTestPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        apply<AndroidCommonPlugin>()
        configure<BaseExtension> {
            defaultConfig {
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                // `useTestStorageService` enables the ability to store files when capturing screenshots.
                // `clearPackageData` makes the Android Test Orchestrator run its "pm clear" command after
                // each test invocation. This command ensures that the app's state is completely cleared
                // between tests.
                testInstrumentationRunnerArguments += mapOf(
                    "useTestStorageService" to "true",
                    "clearPackageData" to "true"
                )
                testOptions.execution = "ANDROIDX_TEST_ORCHESTRATOR"
                testOptions.managedDevices.localDevices.create("pixel2api34") {
                    device = "Pixel 2"
                    apiLevel = 34
                    systemImageSource = "aosp-atd"
                }
            }
        }
        dependencies {
            "androidTestUtil"(libs.test.orchestrator)
            "androidTestUtil"(libs.test.services)
            "androidTestImplementation"(project(":core:android-test"))
        }
    }
}