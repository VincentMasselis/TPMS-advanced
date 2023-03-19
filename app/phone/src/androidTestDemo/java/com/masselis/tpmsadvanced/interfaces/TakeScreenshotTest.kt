package com.masselis.tpmsadvanced.interfaces

import android.app.UiModeManager.MODE_NIGHT_NO
import android.app.UiModeManager.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
internal class TakeScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule(RootActivity::class.java)

    private fun capture(name: String) = composeTestRule
        .onRoot()
        .captureToImage()
        .asAndroidBitmap()
        .writeToTestStorage(name)

    @Before
    fun setup() = runTest {
        composeTestRule.onNodeWithTag("vehicle_dropdown").performClick()
        try {
            composeTestRule.onNodeWithTag("Motorcycle").assertExists()
            composeTestRule.onNodeWithTag("vehicle_dropdown").performClick()
        } catch (_: AssertionError) {
            composeTestRule.onNodeWithTag("add_vehicle").performClick()
            composeTestRule.onNodeWithTag("vehicle_name").performTextInput("Motorcycle")
            composeTestRule.onNodeWithTag(Vehicle.Kind.MOTORCYCLE.name).performClick()
            composeTestRule.onNodeWithText("Add").performClick()
            //delay(1.seconds)
        }
    }

    @Test
    fun lightModeScreenshots() = runTest {
        chooseVehicle("My car")
        takeScreenshots(AppCompatDelegate.MODE_NIGHT_NO)
    }

    @Test
    fun darkModeScreenshots() = runTest {
        chooseVehicle("Motorcycle")
        takeScreenshots(AppCompatDelegate.MODE_NIGHT_YES)
    }

    private suspend fun takeScreenshots(@AppCompatDelegate.NightMode mode: Int) = runTest {
        composeTestRule.activityRule.scenario.apply {
            onActivity {
                AppCompatDelegate.setDefaultNightMode(mode)
            }
        }
        val prefix = when (mode) {
            MODE_NIGHT_NO -> "light_"
            MODE_NIGHT_YES -> "dark_"
            else -> throw IllegalArgumentException()
        }
        delay(500.milliseconds)
        capture("${prefix}main")
        composeTestRule.onNodeWithTag("settings").performClick()
        delay(500.milliseconds)
        capture("${prefix}settings")
    }

    private fun chooseVehicle(name: String) {
        composeTestRule.onNodeWithTag("vehicle_dropdown").performClick()
        composeTestRule.onNodeWithTag(name).performClick()
    }
}