package com.masselis.tpmsadvanced.gitflow

import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters

internal abstract class IsSemVerValueSource : ValueSource<Boolean, IsSemVerValueSource.Params> {

    interface Params : ValueSourceParameters {
        val version: Property<String>
    }

    override fun obtain(): Boolean = versionRegex.matches(parameters.version.get())

    companion object {
        private val versionRegex =
            Regex("/^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?\$/gm")
    }
}