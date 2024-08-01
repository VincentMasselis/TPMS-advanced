package com.masselis.tpmsadvanced.core.ui

import android.content.Intent
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertContentEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

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

    @Test
    fun checkNullableIsStillNull(): Unit = test().use { scenario ->
        scenario
            .onActivity {
                assertNull(it.nullableStringSavedState)
            }
            .recreate()
            .onActivity {
                assertNull(it.nullableStringSavedState)
            }
    }

    @Test
    fun checkThreadSafety(): Unit = test().use { scenario ->
        scenario.onActivity {
            runBlocking {
                awaitAll(
                    async(IO) { it.heavyDefaultThreadSafe },
                    async(IO) { it.heavyDefaultThreadSafe }
                )
            }
            assertContentEquals(
                listOf(
                    // Default value lambda is not computing at the beginning
                    0,
                    // Default value lambda is currently computing, this value must not be higher
                    // than 1 because 2 means the lambda is called twice
                    1,
                    // Default value is computed, the lambda should not be used anymore
                    0
                ),
                it.concurrentThreadCount,
            )
        }
    }
}

