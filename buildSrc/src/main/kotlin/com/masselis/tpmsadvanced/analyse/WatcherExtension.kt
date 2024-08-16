package com.masselis.tpmsadvanced.analyse

import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

public abstract class WatcherExtension(
    private val projectPath: String
) {
    /**
     * Set which contains every watched package when the obfuscated apk is compiled. Use
     * [packageWatchList] when your package hierarchy is uncommon and requires your to use a regex.
     * If your module holds code in a single package consider using [watchPackageAndSubPackages]
     * instead for a seamless grade configuration.
     */
    public abstract val packageWatchList: SetProperty<Regex>

    /**
     *  Minimal code obfuscation required for watched packages.
     *  Default value is at least 80% of obfuscated code.
     */
    public abstract val minimalObfuscationPercentageOpt: Property<Optional<Fraction>>

    /**
     *  Minimal code obfuscation required for watched packages.
     *  Default value is at least 80% of obfuscated code.
     *
     *  Identical to [minimalObfuscationPercentageOpt] with some syntax sugar
     */
    public var minimalObfuscationPercentage: Fraction?
        get() = minimalObfuscationPercentageOpt.get().getOrNull()
        set(value) = minimalObfuscationPercentageOpt.set(Optional.ofNullable(value))

    /**
     * Watch for the current package and sub-packages, for instance is [packages] is set to
     * `my.package`, the package `my.package` will be watched along with any subpackage like
     * `my.package.sub` or `my.package.sub.inner`
     */
    public fun watchPackageAndSubPackages(vararg packages: String): Unit = packages
        .forEach { `package` ->
            packageWatchList.add("^${`package`.replace(".", "\\.")}.*$".toRegex())
        }

    @Suppress("LeakingThis")
    internal val content: Provider<Content> = packageWatchList
        .zip(minimalObfuscationPercentageOpt) { l, r -> Content(projectPath, l, r.getOrNull()) }

    internal data class Content(
        val projectPath: String,
        val packageWatchList: Set<Regex>,
        val minimalObfuscationPercentage: Fraction?
    ) : java.io.Serializable
}
