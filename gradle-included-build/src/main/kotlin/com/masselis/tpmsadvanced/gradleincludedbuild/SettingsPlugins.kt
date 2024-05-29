package com.masselis.tpmsadvanced.gradleincludedbuild

import com.gradle.enterprise.gradleplugin.GradleEnterpriseExtension
import com.gradle.enterprise.gradleplugin.GradleEnterprisePlugin
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.the

@Suppress("unused")
abstract class SettingsPlugins : Plugin<Settings> {
    override fun apply(target: Settings): Unit = with(target) {
        plugins.apply(GradleEnterprisePlugin::class)
        the<GradleEnterpriseExtension>().apply {
            buildScan {
                termsOfServiceUrl = "https://gradle.com/terms-of-service"
                termsOfServiceAgree = "yes"
            }
        }
    }
}
