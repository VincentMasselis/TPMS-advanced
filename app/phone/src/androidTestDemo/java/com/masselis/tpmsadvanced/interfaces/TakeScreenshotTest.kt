package com.masselis.tpmsadvanced.interfaces

import android.app.UiModeManager.MODE_NIGHT_NO
import android.app.UiModeManager.MODE_NIGHT_YES
import android.graphics.Bitmap.CompressFormat.PNG
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.test.ComposeTimeoutException
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.runner.screenshot.Screenshot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
internal class TakeScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule(RootActivity::class.java)

    private fun capture(name: String) = Screenshot.capture()
        .apply { format = PNG }
        .apply { setName(name) }
        .process()

    @Test
    fun lightModeScreenshots() = runTest {
        takeScreenshots(AppCompatDelegate.MODE_NIGHT_NO)
    }

    @Test
    fun darkModeScreenshots() = runTest {
        takeScreenshots(AppCompatDelegate.MODE_NIGHT_YES)
    }

    private suspend fun takeScreenshots(@AppCompatDelegate.NightMode mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
        val prefix = when (mode) {
            MODE_NIGHT_NO -> "light_"
            MODE_NIGHT_YES -> "dark_"
            else -> throw IllegalArgumentException()
        }
        sleep(500.milliseconds)
        capture("${prefix}main.png")
        composeTestRule.onNodeWithTag("settings").performClick()
        sleep(500.milliseconds)
        capture("${prefix}settings.png")
    }

    private suspend fun sleep(duration: Duration): Unit = try {
        require(duration <= 1.seconds)
        composeTestRule.awaitIdle()
        val end = System.nanoTime() + duration.inWholeNanoseconds
        composeTestRule.waitUntil { System.nanoTime() > end }
    } catch (_: ComposeTimeoutException) {

    }
}