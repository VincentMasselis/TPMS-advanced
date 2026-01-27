
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.the

public class AndroidAppPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        plugins.apply("com.android.application")
        apply<AndroidCommonPlugin>()
        the<ApplicationAndroidComponentsExtension>().ignoreDemoRelease()
    }
}