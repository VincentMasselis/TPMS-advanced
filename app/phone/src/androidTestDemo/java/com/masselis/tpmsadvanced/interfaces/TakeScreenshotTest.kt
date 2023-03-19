package com.masselis.tpmsadvanced.interfaces

import android.app.UiModeManager.MODE_NIGHT_NO
import android.app.UiModeManager.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags.dialogAddVehicleAddButton
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags.dialogAddVehicleKindRadio
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags.dialogAddVehicleTextField
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags.dropdownEntry
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags.dropdownEntryAddVehicle
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags.dropdownMenu
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import com.masselis.tpmsadvanced.data.car.model.Vehicle.Kind.MOTORCYCLE
import com.masselis.tpmsadvanced.interfaces.composable.HomeTags.settings
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
        composeTestRule.onNodeWithTag(dropdownMenu).performClick()
        try {
            composeTestRule.onNodeWithTag(dropdownEntry("Motorcycle")).assertExists()
            composeTestRule.onNodeWithTag(dropdownMenu).performClick()
        } catch (_: AssertionError) {
            composeTestRule.onNodeWithTag(dropdownEntryAddVehicle).performClick()
            composeTestRule.onNodeWithTag(dialogAddVehicleTextField).performTextInput("Motorcycle")
            composeTestRule.onNodeWithTag(dialogAddVehicleKindRadio(MOTORCYCLE)).performClick()
            composeTestRule.onNodeWithTag(dialogAddVehicleAddButton).performClick()
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
        composeTestRule.onNodeWithTag(settings).performClick()
        delay(500.milliseconds)
        capture("${prefix}settings")
    }

    private fun chooseVehicle(name: String) {
        composeTestRule.onNodeWithTag(dropdownMenu).performClick()
        composeTestRule.onNodeWithTag(dropdownEntry(name)).performClick()
    }
}