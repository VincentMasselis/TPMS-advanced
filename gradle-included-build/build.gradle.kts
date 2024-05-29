plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.gradle.entreprise)
}

gradlePlugin {
    val dependenciesPlugin by plugins.creating {
        id = "settings-plugins"
        implementationClass = "com.masselis.tpmsadvanced.gradleincludedbuild.SettingsPlugins"
    }
}
