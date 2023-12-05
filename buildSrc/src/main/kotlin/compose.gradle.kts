import com.android.build.gradle.BaseExtension

plugins {
    com.android.base
    id("detekt")
}

the<BaseExtension>().apply android@{
    buildFeatures.compose = true
    composeOptions {
        kotlinCompilerExtensionVersion =
            versionCatalog.findVersion("composeCompiler").get().toString()
    }
    dependencies {
        "detektPlugins"(versionCatalog.findLibrary("compose-detekt").get())
    }
}