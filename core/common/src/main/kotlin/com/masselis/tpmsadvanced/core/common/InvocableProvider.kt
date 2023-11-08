package com.masselis.tpmsadvanced.core.common

import javax.inject.Provider

public inline operator fun <reified T> Provider<T>.invoke(): T = get()
