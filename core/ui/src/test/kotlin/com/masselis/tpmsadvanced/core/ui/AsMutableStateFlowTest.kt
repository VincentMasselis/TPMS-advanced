@file:OptIn(ExperimentalCoroutinesApi::class)

package com.masselis.tpmsadvanced.core.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.masselis.tpmsadvanced.core.test.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class AsMutableStateFlowTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    /**
     * Non-null [String] with a value
     */
    @Test
    fun normal() = runTest {
        @Suppress("RedundantExplicitType") val test: String = "TEST"
        assert(test == MutableLiveData(test).asMutableStateFlow().value)
        coroutineContext.cancelChildren()
    }

    /**
     * Nullable [String] with a value
     */
    @Suppress("RedundantNullableReturnType")
    @Test
    fun normalNullable() = runTest {
        val test: String? = "TEST"
        assert(test == MutableLiveData(test).asMutableStateFlow().value)
        coroutineContext.cancelChildren()
    }

    /**
     * Nullable [String] set to null
     */
    @Test
    fun normalNullableWithNull() = runTest {
        val test: String? = null
        assert(test == MutableLiveData(test).asMutableStateFlow().value)
        coroutineContext.cancelChildren()
    }

    /**
     * Non-null [String] but initialization is missing
     */
    @Test
    fun notInitialized() = runTest {
        assertFailsWith<IllegalArgumentException> {
            MutableLiveData<String>().asMutableStateFlow()
        }
    }


    /**
     * Nullable [String] but initialization is missing
     */
    @Test
    fun nonInitializedNullable() = runTest {
        assertFailsWith<IllegalArgumentException> {
            MutableLiveData<String?>().asMutableStateFlow()
        }
    }

    /**
     * Check that updating a live data also update the state flow and vice-versa
     */
    @Test
    fun twoWayBinding() = runTest {
        val liveData = MutableLiveData("TEST")
        val mutableStateFlow = liveData.asMutableStateFlow()

        assertEquals("TEST", mutableStateFlow.value)
        assertEquals("TEST", liveData.value)

        mutableStateFlow.value = "TEST2"
        advanceUntilIdle()
        assertEquals("TEST2", mutableStateFlow.value)
        assertEquals("TEST2", liveData.value)

        liveData.postValue("TEST3")
        advanceUntilIdle()
        assertEquals("TEST3", mutableStateFlow.value)
        assertEquals("TEST3", liveData.value)

        coroutineContext.cancelChildren()
    }

    /**
     * Non-null [String] with a value
     */
    @Test
    fun normalFromSavedStateHandle() = runTest {
        val savedStateHandle = SavedStateHandle()
        savedStateHandle["TEST"] = "TEST"
        val mutableStateFlow = savedStateHandle.getMutableStateFlow<String>("TEST")
        assertEquals("TEST", mutableStateFlow.value)
        coroutineContext.cancelChildren()
    }

    /**
     * Nullable [String] with a value
     */
    @Test
    fun normalNullableFromSavedStateHandle() = runTest {
        val savedStateHandle = SavedStateHandle()
        savedStateHandle["TEST"] = "TEST" as String?
        val mutableStateFlow = savedStateHandle.getMutableStateFlow<String?>("TEST")
        assertEquals("TEST", mutableStateFlow.value)
        coroutineContext.cancelChildren()
    }

    /**
     * Nullable [String] without value
     */
    @Test
    fun normalNullableWithNullFromSavedStateHandle() = runTest {
        val savedStateHandle = SavedStateHandle()
        savedStateHandle["TEST"] = null as String?
        val mutableStateFlow = savedStateHandle.getMutableStateFlow<String?>("TEST")
        assertEquals(null, mutableStateFlow.value)
        coroutineContext.cancelChildren()
    }

    /**
     * No content in [SavedStateHandle], [getMutableStateFlow] creates it
     */
    @Test
    fun normalWithInitializationFromSavedStateHandle() = runTest {
        val mutableStateFlow = SavedStateHandle().getMutableStateFlow("TEST") { "TEST" }
        assertEquals("TEST", mutableStateFlow.value)
        coroutineContext.cancelChildren()
    }

    /**
     * No content in [SavedStateHandle], [getMutableStateFlow] forgets to create it
     */
    @Test
    fun normalWithoutInitializationFromSavedStateHandle() = runTest {
        assertFailsWith<IllegalArgumentException> {
            SavedStateHandle().getMutableStateFlow<String>("TEST")
        }
        coroutineContext.cancelChildren()
    }
}
