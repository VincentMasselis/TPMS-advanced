package com.masselis.tpmsadvanced.core.database

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlin.coroutines.CoroutineContext


@OptIn(DelicateCoroutinesApi::class)
public inline fun <reified T : Any> Query<T>.asStateFlowList(
    context: CoroutineContext = IO,
    scope: CoroutineScope = GlobalScope,
    started: SharingStarted = WhileSubscribed()
): StateFlow<List<T>> = asChillFlow()
    .mapToList(context)
    .stateIn(scope, started, executeAsList())

@OptIn(DelicateCoroutinesApi::class)
public inline fun <reified T : Any> Query<T>.asStateFlowOne(
    context: CoroutineContext = IO,
    scope: CoroutineScope = GlobalScope,
    started: SharingStarted = WhileSubscribed()
): StateFlow<T> = asChillFlow()
    .mapToOne(context)
    .stateIn(scope, started, executeAsOne())

@OptIn(DelicateCoroutinesApi::class)
public inline fun <reified T : Any> Query<T>.asStateFlowOneOrNull(
    context: CoroutineContext = IO,
    scope: CoroutineScope = GlobalScope,
    started: SharingStarted = WhileSubscribed()
): StateFlow<T?> = asChillFlow()
    .mapToOneOrNull(context)
    .stateIn(scope, started, executeAsOneOrNull())

/** Identical to [app.cash.sqldelight.coroutines.asFlow] but this one doesn't emit at launch */
public fun <T : Any> Query<T>.asChillFlow(): Flow<Query<T>> = flow {
    val channel = Channel<Unit>(CONFLATED)

    val listener = Query.Listener {
        channel.trySend(Unit)
    }

    addListener(listener)
    try {
        for (ignored in channel) {
            emit(this@asChillFlow)
        }
    } finally {
        removeListener(listener)
    }
}
