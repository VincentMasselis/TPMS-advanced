package com.masselis.tpmsadvanced.analyse

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.ApplicationVariant
import com.android.build.api.variant.VariantSelector
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import java.util.concurrent.atomic.AtomicBoolean

public interface AppExtension {

    /**
     * Set which [ApplicationVariant] to use to compile clear apk and obfuscated apk by its name.
     * If the variant is not found, apk comparison task fails.
     *
     * ```kotlin
     * // build.gradle.kts
     * obfuscationAnalyser {
     *     setVariant("prodDebug", "prodRelease")
     * }
     * ```
     */
    public fun Project.setVariant(clear: String, obfuscated: String) {
        configure<ApplicationAndroidComponentsExtension> {
            setVariant(selector().withName(clear), selector().withName(obfuscated))
        }
    }

    /**
     * Set which [ApplicationVariant] to use to compile clear apk and obfuscated apk by its name.
     * If the variant is not found, apk comparison task fails.
     *
     * ```kotlin
     * // build.gradle.kts
     * obfuscationAnalyser {
     *     androidComponents {
     *         setVariant(
     *             selector().withBuildType("debug"),
     *             selector().withBuildType("release")
     *         )
     *     }
     * }
     * ```
     */
    public fun Project.setVariant(clear: VariantSelector, obfuscated: VariantSelector) {
        configure<ApplicationAndroidComponentsExtension> {
            onVariants(clear, singleInvocation { this@AppExtension.clear = it })
            onVariants(obfuscated, singleInvocation { this@AppExtension.obfuscated = it })
        }
    }

    private fun singleInvocation(
        block: (ApplicationVariant) -> Unit
    ) = object : (ApplicationVariant) -> Unit {
        val blockCalled = AtomicBoolean(false)
        override fun invoke(variant: ApplicationVariant) {
            if (blockCalled.compareAndSet(false, true))
                block(variant)
            else
                throw GradleException("Multiple \"ApplicationVariant\" instances were found for the same selector. Current variant name \"${variant.name}\". You have to update your selector to make it more strict in order to return only one \"ApplicationVariant\" when calling \"onVariants(selector) { }\"")
        }
    }

    /**
     * [ApplicationVariant] which compiles a non obfuscated apk, like a debug one. Instead of using
     * this property, you should use [setVariant] instead, it's less error prone.
     */
    public val clear: Property<ApplicationVariant>

    /**
     * [ApplicationVariant] which compiles an obfuscated apk, like a release one. Instead of using
     * this property, you should use [setVariant] instead, it's less error prone.
     */
    public val obfuscated: Property<ApplicationVariant>

    /**
     * Default minimal obfuscation percentage accepted by the plugin for each module. This value is
     * ignored if [LibraryExtension.minimalObfuscationPercentage] holds a value.
     */
    public val defaultMinimalModuleObfuscationPercentage: Property<Fraction>

    /** Default minimal obfuscation percentage accepted for the whole app */
    public val minimalAppObfuscationPercentage: Property<Fraction>
}