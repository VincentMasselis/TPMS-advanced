
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.the

public class AndroidLibPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        plugins.apply("com.android.library")
        apply<AndroidCommonPlugin>()
        with(file("consumer-rules.pro")) {
            if (exists()) {
                the<LibraryExtension>().defaultConfig.consumerProguardFile(this)
            }
        }
    }
}