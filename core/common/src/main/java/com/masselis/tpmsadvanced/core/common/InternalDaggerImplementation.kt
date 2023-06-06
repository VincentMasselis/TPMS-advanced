package com.masselis.tpmsadvanced.core.common

import kotlin.annotation.AnnotationRetention.BINARY
import kotlin.annotation.AnnotationTarget.FUNCTION

@Retention(value = BINARY)
@Target(FUNCTION)
@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
public annotation class InternalDaggerImplementation
