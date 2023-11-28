import com.android.build.gradle.BaseExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.the

plugins {
    com.android.base
}

the<BaseExtension>().apply android@{
    buildFeatures.compose = true
    composeOptions {
        kotlinCompilerExtensionVersion =
            versionCatalog.findVersion("composeCompiler").get().toString()
    }
    dependencies {
        "lintChecks"("com.slack.lint.compose:compose-lint-checks:1.2.0")
    }
}