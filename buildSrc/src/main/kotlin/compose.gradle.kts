
import com.android.build.gradle.BaseExtension

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
        "lintChecks"(versionCatalog.findLibrary("compose-lint").get())
    }
}