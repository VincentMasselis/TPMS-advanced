
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

public class DetektPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        plugins.apply("io.gitlab.arturbosch.detekt")
        configure<DetektExtension> {
            buildUponDefaultConfig = true
            config.from("${rootProject.projectDir}/buildSrc/detekt-config.yml")
        }
    }
}