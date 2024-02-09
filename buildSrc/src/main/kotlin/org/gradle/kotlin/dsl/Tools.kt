package org.gradle.kotlin.dsl

import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import kotlin.reflect.KClass

public inline fun <T : Any, P : ValueSourceParameters> ProviderFactory.from(
    valueSourceType: KClass<out ValueSource<T, P>>,
    crossinline configuration: P.() -> Unit
): Provider<T> = this.of(valueSourceType) { parameters.configuration() }

public fun <T : Any> ProviderFactory.from(
    valueSourceType: KClass<out ValueSource<T, ValueSourceParameters.None>>,
): Provider<T> = this.of(valueSourceType) { }