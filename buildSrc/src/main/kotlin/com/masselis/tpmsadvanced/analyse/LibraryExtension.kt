package com.masselis.tpmsadvanced.analyse

import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import java.io.Serializable
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

@Suppress("MemberVisibilityCanBePrivate")
public abstract class LibraryExtension internal constructor(
    public val projectPath: String,
) : Serializable {
    /**
     * Set which contains every watched package when the obfuscated apk is compiled. Use
     * [packageWatchList] when your package hierarchy is uncommon and requires your to use a regex.
     * If your module holds code in a single package consider using [watchPackageAndSubPackages]
     * instead for a seamless grade configuration.
     *
     * By default, [packageWatchList] contains every package from your module's sources.
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
     * Watch for the current package and sub-packages, for instance if [packages] is set to
     * `my.package`, the package `my.package` will be watched along with any subpackage like
     * `my.package.sub` or `my.package.sub.inner`
     */
    public fun watchPackageAndSubPackages(vararg packages: String): Unit = packages
        .forEach { `package` ->
            packageWatchList.add("^${`package`.replace(".", "\\.")}.*$".toRegex())
        }

    /**
     * Watch for the filled package without checking sub-packages. For instance if [packages] is set
     * to `my.package`, only `my.package` will be watched, `my.package.sub` will be ignored.
     */
    public fun watchPackage(vararg packages: String): Unit = packages
        .forEach { `package` ->
            packageWatchList.add("^${`package`.replace(".", "\\.")}$".toRegex())
        }

    internal data class Data(
        val projectPath: String,
        val packageWatchList: Set<Regex>,
        val minimalObfuscationPercentage: Fraction?
    ) : Serializable {
        constructor(ext: LibraryExtension) : this(
            ext.projectPath,
            ext.packageWatchList.get(),
            ext.minimalObfuscationPercentage
        )
    }
}
