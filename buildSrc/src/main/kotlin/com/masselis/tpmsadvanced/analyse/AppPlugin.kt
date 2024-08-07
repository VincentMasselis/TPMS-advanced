package com.masselis.tpmsadvanced.analyse

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.ApplicationVariant
import com.android.build.api.variant.VariantOutputConfiguration.OutputType.SINGLE
import com.android.build.api.variant.impl.VariantOutputImpl
import com.masselis.tpmsadvanced.analyse.Fraction.Companion.fraction
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.invocation.Gradle
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.registerIfAbsent
import org.gradle.kotlin.dsl.setProperty
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType

public class AppPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        val ext = extensions.create<AppExtension>("obfuscationAssertionsDefault")
        ext.clear.convention(provider { throw GradleException("The gradle property \"obfuscationAssertionsDefault.clear\" is missing") })
        ext.obfuscated.convention(provider { throw GradleException("The gradle property \"obfuscationAssertionsDefault.obfuscated\" is missing") })
        ext.defaultMinimalObfuscationPercentage.convention(0.8f.fraction)
        configure<ApplicationAndroidComponentsExtension> {
            onVariants { it.createService(project) }
        }
        val computeClearDecompiled = tasks
            .create<ComputeDecompiled>("computeClearDecompiled") {
                dependsOn(ext.clear.assembleTaskName())
                service = ext.clear.findService(gradle, project.path)
            }
        val computeObfuscatedDecompiled = tasks
            .create<ComputeDecompiled>("computeObfuscatedDecompiled") {
                dependsOn(ext.obfuscated.assembleTaskName())
                service = ext.obfuscated.findService(gradle, project.path)
            }
        val modulesContent = objects.setProperty<WatcherExtension.Content>()
        recursivelySearchForModulePlugin(modulesContent)
        tasks.create<CompareDecompiled>("compareApk") {
            dependsOn(computeClearDecompiled, computeObfuscatedDecompiled)
            clear = ext.clear.findService(gradle, project.path)
            obfuscated = ext.obfuscated.findService(gradle, project.path)
            defaultObfuscationPercentage = ext.defaultMinimalObfuscationPercentage
            this.modules = modulesContent
        }
    }

    private fun ApplicationVariant.createService(
        project: Project,
    ): Provider<DecompilerService> = project
        .gradle
        .sharedServices
        .registerIfAbsent(
            "${project.path}-decompiler-service-${this@createService.name}",
            DecompilerService::class
        ) {
            parameters.apkToDecompile = outputs
                .map { it as VariantOutputImpl }
                // Learn more: https://developer.android.com/build/configure-apk-splits#configure-split
                .single { it.outputType == SINGLE }
                .outputFileName
                .flatMap { fileName ->
                    project.layout.buildDirectory.file(
                        "outputs" +
                                "/apk" +
                                "/${flavorName!!}" +
                                "/${buildType!!}" +
                                "/${fileName.substringBeforeLast('.') + ".apk"}"
                    )
                }
        }

    private fun Provider<ApplicationVariant>.findService(
        gradle: Gradle,
        path: String,
    ) = this
        .flatMap { variant ->
            gradle
                .sharedServices
                .registrations["${path}-decompiler-service-${variant.name}"]
                .service
        }
        .map { it as DecompilerService }

    private fun Provider<ApplicationVariant>.assembleTaskName() = map { variant ->
        "assemble${variant.name.capitalized()}"
    }

    private fun Project.recursivelySearchForModulePlugin(modules: SetProperty<WatcherExtension.Content>) {
        // Compilation fails without this line
        @Suppress("UNCHECKED_CAST") val properties = properties as MutableMap<String, Any?>
        if (properties.containsKey(MODULE_VISITED_KEY)) return
        properties[MODULE_VISITED_KEY] = Unit

        plugins.matching { it is WatcherPlugin }.whenPluginAdded {
            modules.add(the<WatcherExtension>().content)
        }
        configurations.configureEach {
            if (name.endsWith("implementation", true) || name.endsWith("api", true))
                dependencies.withType<ProjectDependency>().whenObjectAdded {
                    dependencyProject.recursivelySearchForModulePlugin(modules)
                }
        }
    }

    public companion object {
        private const val MODULE_VISITED_KEY =
            "com.masselis.tpmsadvanced.analyse.AppPlugin.isModuleVisited"
    }
}