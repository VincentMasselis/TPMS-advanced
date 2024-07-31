package com.masselis.tpmsadvanced.core.ui

import android.content.Intent
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.seconds

@RunWith(AndroidJUnit4::class)
internal class SavedStateTest {

    private fun test() = launch<SavedStateActivity>(
        Intent(getApplicationContext(), SavedStateActivity::class.java)
    )

    @Test
    fun checkValueIsSaved(): Unit = test().use { scenario ->
        scenario
            .onActivity {
                assert(it.booleanSavedState.not())
                it.booleanSavedState = true
            }
            .recreate()
            .onActivity {
                assert(it.booleanSavedState)
            }
    }

    @Test
    fun checkDefaultLambdaNotCalledAfterRestore(): Unit = test().use { scenario ->
        scenario
            .onActivity {
                assert(it.booleanSavedStateLambdaCalled.not())
                it.booleanSavedState
                assert(it.booleanSavedStateLambdaCalled)
            }
            .recreate()
            .onActivity {
                assert(it.booleanSavedStateLambdaCalled.not())
                it.booleanSavedState
                assert(it.booleanSavedStateLambdaCalled.not())
            }
    }

    @Test
    fun checkDefaultLambdaCalledOnlyWhenAskingValueForFirstTime(): Unit = test().use { scenario ->
        scenario
            .onActivity {
                assert(it.booleanSavedStateLambdaCalled.not())
            }
            .recreate()
            .onActivity {
                assert(it.booleanSavedStateLambdaCalled.not())
                it.booleanSavedState
                assert(it.booleanSavedStateLambdaCalled)
            }
    }

    @Test
    fun checkNullableStringIsSaved(): Unit = test().use { scenario ->
        scenario
            .onActivity {
                assertNull(it.nullableStringSavedState)
                it.nullableStringSavedState = "MOCK"
            }
            .recreate()
            .onActivity {
                assertNotNull(it.nullableStringSavedState)
            }
    }

    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun checkThreadSafety(): Unit = test().use { scenario ->
        scenario.onActivity {
            it.concurrentThread
                .withIndex()
                .onEach { (index, value) ->
                    when (index) {
                        // Default value is not computing at the beginning
                        0 -> assertEquals(0, value)
                        // Default value is currently computing, this value must not be higher than
                        // 1 because 2 means the default value lambdas is called twice
                        1 -> assertEquals(1, value)
                        // Default value is computed, the lambda should not be used anymore
                        2 -> assertEquals(0, value)
                        else -> error("Unknown index $index with value $value")
                    }
                }
                .launchIn(GlobalScope)
            runBlocking {
                awaitAll(
                    async(IO) { it.heavyDefaultThreadSafe },
                    async(IO) { it.heavyDefaultThreadSafe }
                )
            }
        }
    }

    @Test
    fun checkNonThreadSafety(): Unit = test().use { scenario ->
        scenario.onActivity {
            runBlocking {
                awaitAll(
                    // Because with don't have any thread safety, the concurrent counter should peak
                    // to 2
                    async(IO) { withTimeout(1.seconds) { it.concurrentThread.first { it == 2 } } },
                    async(IO) { it.heavyDefaultNonThreadSafe },
                    async(IO) { it.heavyDefaultNonThreadSafe }
                )
            }
        }
    }
}

