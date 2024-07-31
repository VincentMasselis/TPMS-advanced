package com.masselis.tpmsadvanced.core.ui

import android.content.Intent
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
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
                assert(it.defaultLambdaCalled.not())
                it.booleanSavedState
                assert(it.defaultLambdaCalled)
            }
            .recreate()
            .onActivity {
                assert(it.defaultLambdaCalled.not())
                it.booleanSavedState
                assert(it.defaultLambdaCalled.not())
            }
    }

    @Test
    fun checkDefaultLambdaCalledOnlyWhenAskingValueForFirstTime(): Unit = test().use { scenario ->
        scenario
            .onActivity {
                assert(it.defaultLambdaCalled.not())
            }
            .recreate()
            .onActivity {
                assert(it.defaultLambdaCalled.not())
                it.booleanSavedState
                assert(it.defaultLambdaCalled)
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
}

