package com.masselis.tpmsadvanced.core.database

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlin.coroutines.CoroutineContext

public inline fun <reified T : Any> Query<T>.asListFlow(
    context: CoroutineContext = IO
): Flow<List<T>> = asFlow().mapToList(context)

public inline fun <reified T : Any> Query<T>.asOneFlow(
    context: CoroutineContext = IO
): Flow<T> = asFlow().mapToOne(context)

public inline fun <reified T : Any> Query<T>.asOneOrNullFlow(
    context: CoroutineContext = IO
): Flow<T?> = asFlow().mapToOneOrNull(context)

public inline fun <reified T : Any> Query<T>.asListChillFlow(
    context: CoroutineContext = IO
): Flow<List<T>> = asChillFlow().mapToList(context)

public inline fun <reified T : Any> Query<T>.asOneChillFlow(
    context: CoroutineContext = IO
): Flow<T> = asChillFlow().mapToOne(context)

public inline fun <reified T : Any> Query<T>.asOneOrNullChillFlow(
    context: CoroutineContext = IO
): Flow<T?> = asChillFlow().mapToOneOrNull(context)

public inline fun <reified T : Any> Query<T>.asListStateFlow(
    scope: CoroutineScope,
    started: SharingStarted
): StateFlow<List<T>> = asFlow()
    .mapToList(scope.coroutineContext)
    .stateIn(scope, started, executeAsList())

public inline fun <reified T : Any> Query<T>.asOneStateFlow(
    scope: CoroutineScope,
    started: SharingStarted
): StateFlow<T> = asFlow()
    .mapToOne(scope.coroutineContext)
    .stateIn(scope, started, executeAsOne())

public inline fun <reified T : Any> Query<T>.asOneOrNullStateFlow(
    scope: CoroutineScope,
    started: SharingStarted
): StateFlow<T?> = asFlow()
    .mapToOneOrNull(scope.coroutineContext)
    .stateIn(scope, started, executeAsOneOrNull())

public inline fun <reified T : Any> Query<T>.asChillFlow(): Flow<Query<T>> = flow {
    val channel = Channel<Unit>(Channel.CONFLATED)

    val listener = Query.Listener {
        channel.trySend(Unit)
    }

    addListener(listener)
    try {
        for (item in channel) {
            emit(this@asChillFlow)
        }
    } finally {
        removeListener(listener)
    }
}
