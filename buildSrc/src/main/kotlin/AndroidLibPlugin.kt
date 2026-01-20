
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.the

public class AndroidLibPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        plugins.apply("com.android.library")
        apply<AndroidCommonPlugin>()
        the<LibraryExtension>().defaultConfig.consumerProguardFile("consumer-rules.pro")
        the<LibraryAndroidComponentsExtension>().ignoreDemoRelease()
    }
}