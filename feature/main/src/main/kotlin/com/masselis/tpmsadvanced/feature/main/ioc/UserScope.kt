package com.masselis.tpmsadvanced.feature.main.ioc

import dev.zacsweers.metro.Qualifier
import kotlin.reflect.KClass

public abstract class UserScope private constructor()

@Qualifier
public annotation class QualifierFor(val scope: KClass<*>)