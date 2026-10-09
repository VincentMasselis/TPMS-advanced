package com.masselis.tpmsadvanced.core.common

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class CombineStatesTest {

    private lateinit var first: MutableStateFlow<Int>
    private lateinit var second: MutableStateFlow<String>
    private var transformCount = 0

    @Before
    fun setup() {
        first = MutableStateFlow(1)
        second = MutableStateFlow("a")
        transformCount = 0
    }

    private fun test(): StateFlow<String> = combineStates(first, second) { a, b ->
        transformCount++
        "$a$b"
    }

    @Test
    fun `value is updated synchronously without any collector`() {
        val combined = test()
        assertEquals("1a", combined.value)
        first.value = 2
        assertEquals("2a", combined.value)
        second.value = "b"
        assertEquals("2b", combined.value)
    }

    @Test
    fun `collect emits the current value then every change`() = runTest {
        test().test {
            assertEquals("1a", awaitItem())
            first.value = 2
            assertEquals("2a", awaitItem())
            second.value = "b"
            assertEquals("2b", awaitItem())
        }
    }

    @Test
    fun `equal consecutive results are not emitted twice`() = runTest {
        combineStates(first, second) { a, _ -> a % 2 }.test {
            assertEquals(1, awaitItem())
            first.value = 3
            second.value = "b"
            first.value = 4
            assertEquals(0, awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun `transform is not invoked again while inputs are unchanged`() {
        val combined = test()
        repeat(10) { combined.value }
        assertEquals(1, transformCount)
        // An equal value keeps the StateFlow's current instance, so the cache still hits
        second.value = "a"
        combined.value
        assertEquals(1, transformCount)
        first.value = 2
        combined.value
        combined.value
        assertEquals(2, transformCount)
    }

    @Test
    fun `null outputs are cached`() {
        var count = 0
        val combined = combineStates(first, second) { _, _ -> null.also { count++ } }
        assertNull(combined.value)
        assertNull(combined.value)
        assertEquals(1, count)
    }

    @Test
    fun `vararg and extension overloads combine every flow`() = runTest {
        val third = MutableStateFlow(3)
        val combined = combineStates(first, MutableStateFlow(2), third) { it.sum() }
        assertEquals(6, combined.value)
        third.value = 10
        assertEquals(13, combined.value)
        assertEquals(13, combined.first())
        assertEquals("1a", first.combineStates(second) { a, b -> "$a$b" }.value)
    }

    @Test
    fun `combining nothing gives a constant state`() = runTest {
        val combined = combineStates(emptyList<StateFlow<Int>>()) { it.size }
        assertEquals(0, combined.value)
        assertEquals(listOf(0), combined.take(1).toList())
    }

    @Test
    fun `combined states can be combined again`() = runTest {
        val nested = combineStates(test(), first) { ab, a -> "$ab-$a" }
        assertEquals("1a-1", nested.value)
        nested.test {
            assertEquals("1a-1", awaitItem())
            first.value = 2
            // Exactly like kotlinx's combine, a diamond dependency may emit a transient glitch
            // before converging, only `value` is guaranteed to be consistent at any time
            assertEquals("2a-2", awaitItem().let { if (it == "2a-1") awaitItem() else it })
        }
        assertEquals("2a-2", nested.value)
    }

    @Test
    fun `value stays consistent with concurrent writers and readers`() = runBlocking {
        val combined = combineStates(first, MutableStateFlow(0)) { a, _ -> a * 2 }
        withContext(Dispatchers.Default) {
            repeat(8) { worker ->
                launch {
                    repeat(10_000) { i ->
                        if (worker % 2 == 0) first.value = i
                        check(combined.value % 2 == 0)
                    }
                }
            }
        }
        assertEquals(first.value * 2, combined.value)
    }
}
