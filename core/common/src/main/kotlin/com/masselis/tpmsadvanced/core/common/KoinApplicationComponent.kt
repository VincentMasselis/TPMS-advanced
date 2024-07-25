package com.masselis.tpmsadvanced.core.common

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication

public fun koinComponent(app: KoinApplication): KoinComponent = object : KoinComponent {
    override fun getKoin(): Koin = app.koin
}

public fun koinApplicationComponent(
    createEagerInstances: Boolean = true,
    appDeclaration: KoinAppDeclaration? = null
): KoinComponent = koinComponent(koinApplication(createEagerInstances, appDeclaration))

public fun koinApplicationComponent(
    appDeclaration: KoinAppDeclaration?
): KoinComponent = koinComponent(koinApplication(true, appDeclaration))

public fun koinApplicationComponent(
    createEagerInstances: Boolean
): KoinComponent = koinComponent(koinApplication(createEagerInstances, null))
