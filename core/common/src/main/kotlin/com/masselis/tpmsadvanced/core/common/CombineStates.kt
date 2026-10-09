package com.masselis.tpmsadvanced.core.common

import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.concurrent.Volatile

/*
 * StateFlow counterparts of kotlinx's `combine` operators, see
 * https://github.com/Kotlin/kotlinx.coroutines/issues/2631 for the context.
 *
 * Unlike `combine(...).stateIn(scope, started, initialValue)`, the returned StateFlow:
 * - doesn't need any CoroutineScope, nothing runs while nobody collects it
 * - has a `value` which is always up to date: right after `upstream.value = x`, reading `value`
 *   already reflects `x`. The `stateIn` approach only catches up once its coroutine is dispatched.
 *
 * Differences with kotlinx's `combine`, imposed by the StateFlow contract:
 * - `transform` is not `suspend` since `value` must be computable synchronously. It must be pure,
 *   cheap and thread-safe: it may run on any thread reading `value` or collecting the flow.
 * - Consecutive equal results are never emitted twice (StateFlow is always "distinct until changed").
 * - Combining an empty collection gives a StateFlow holding `transform(emptyArray())` instead of an
 *   empty flow.
 */

// Same JVM signature than the top-level 2-flows overload, kotlinx works around it the same way
@JvmName("stateFlowCombineStates")
public fun <T1, T2, R> StateFlow<T1>.combineStates(
    flow: StateFlow<T2>,
    transform: (a: T1, b: T2) -> R
): StateFlow<R> = combineStates(this, flow, transform)

@Suppress("UNCHECKED_CAST")
public fun <T1, T2, R> combineStates(
    flow: StateFlow<T1>,
    flow2: StateFlow<T2>,
    transform: (a: T1, b: T2) -> R
): StateFlow<R> = combineStatesInternal(listOf(flow, flow2)) { (a, b) ->
    transform(a as T1, b as T2)
}

@Suppress("UNCHECKED_CAST")
public fun <T1, T2, T3, R> combineStates(
    flow: StateFlow<T1>,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    transform: (T1, T2, T3) -> R
): StateFlow<R> = combineStatesInternal(listOf(flow, flow2, flow3)) { (a, b, c) ->
    transform(a as T1, b as T2, c as T3)
}

@Suppress("UNCHECKED_CAST")
public fun <T1, T2, T3, T4, R> combineStates(
    flow: StateFlow<T1>,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    flow4: StateFlow<T4>,
    transform: (T1, T2, T3, T4) -> R
): StateFlow<R> = combineStatesInternal(listOf(flow, flow2, flow3, flow4)) { (a, b, c, d) ->
    transform(a as T1, b as T2, c as T3, d as T4)
}

@Suppress("UNCHECKED_CAST", "LongParameterList")
public fun <T1, T2, T3, T4, T5, R> combineStates(
    flow: StateFlow<T1>,
    flow2: StateFlow<T2>,
    flow3: StateFlow<T3>,
    flow4: StateFlow<T4>,
    flow5: StateFlow<T5>,
    transform: (T1, T2, T3, T4, T5) -> R
): StateFlow<R> = combineStatesInternal(listOf(flow, flow2, flow3, flow4, flow5)) { (a, b, c, d, e) ->
    transform(a as T1, b as T2, c as T3, d as T4, e as T5)
}

public inline fun <reified T, R> combineStates(
    vararg flows: StateFlow<T>,
    crossinline transform: (Array<T>) -> R
): StateFlow<R> = combineStates(flows.asList(), transform)

public inline fun <reified T, R> combineStates(
    flows: Iterable<StateFlow<T>>,
    crossinline transform: (Array<T>) -> R
): StateFlow<R> = combineStatesInternal(flows.toList()) { values ->
    transform(Array(values.size) { values[it] as T })
}

@PublishedApi
internal fun <R> combineStatesInternal(
    flows: List<StateFlow<*>>,
    transform: (List<Any?>) -> R
): StateFlow<R> = flows
    .takeIf { it.isNotEmpty() }
    ?.let { CombinedStateFlow(it, transform) }
    ?: MutableStateFlow(transform(emptyList())).asStateFlow()

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
private class CombinedStateFlow<R>(
    private val flows: List<StateFlow<*>>,
    private val transform: (List<Any?>) -> R
) : StateFlow<R> {

    // Inputs and output live in a single immutable record replaced at once, so concurrent readers can
    // never observe an output paired with the wrong inputs. A race only costs a redundant transform.
    @Volatile
    private var cache: Cache<R>? = null

    override val value: R
        get() = flows
            .map { it.value }
            .resolve()

    override val replayCache: List<R>
        get() = listOf(value)

    override suspend fun collect(collector: FlowCollector<R>): Nothing {
        combine(flows) { it.asList().resolve() }
            // `distinctUntilChanged()` is a no-op on StateFlow instances, downstream operators rely
            // on us to honor this contract
            .distinctUntilChanged()
            .collect(collector)
        // Unreachable in practice since collecting a StateFlow never completes
        awaitCancellation()
    }

    // Inputs are compared by identity: StateFlow keeps its current instance when an equal value is
    // set, so identity is both exact and O(1), whatever the cost of `equals()`.
    // The elvis works on the record, not on its output, so a cached `null` output isn't a cache miss.
    private fun List<Any?>.resolve(): R = cache
        ?.takeIf { cached -> cached.inputs.size == size && indices.all { cached.inputs[it] === this[it] } }
        .let { it ?: Cache(this, transform(this)).also { new -> cache = new } }
        .output

    private class Cache<R>(val inputs: List<Any?>, val output: R)
}
