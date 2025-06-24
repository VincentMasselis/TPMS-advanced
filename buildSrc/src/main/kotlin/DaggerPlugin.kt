
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

public class DaggerPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        plugins.apply("com.google.devtools.ksp")
        dependencies {
            "implementation"(libs.dagger.lib)
            "ksp"(libs.dagger.compiler)
        }
    }
}