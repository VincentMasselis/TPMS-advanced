import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

public class AndroidLibPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        plugins.apply("com.android.library")
        apply<AndroidCommonPlugin>()
        configure<BaseExtension> {
            defaultConfig {
                consumerProguardFile("consumer-rules.pro")
            }
        }
    }
}