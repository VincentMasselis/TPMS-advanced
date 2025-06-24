import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

public class ComposePlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        plugins.apply("org.jetbrains.kotlin.plugin.compose")
        apply<DetektPlugin>()
        dependencies {
            "detektPlugins"(libs.compose.detekt)
        }
    }
}
