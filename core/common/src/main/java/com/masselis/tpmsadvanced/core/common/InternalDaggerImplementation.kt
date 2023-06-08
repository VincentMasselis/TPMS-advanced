package com.masselis.tpmsadvanced.core.common

import kotlin.annotation.AnnotationRetention.BINARY

@Retention(value = BINARY)
@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
public annotation class InternalDaggerImplementation
