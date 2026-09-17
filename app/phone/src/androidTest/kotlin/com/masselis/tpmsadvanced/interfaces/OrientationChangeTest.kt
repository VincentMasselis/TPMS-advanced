package com.masselis.tpmsadvanced.interfaces

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.device.DeviceInteraction.Companion.setScreenOrientation
import androidx.test.espresso.device.EspressoDevice.Companion.onDevice
import androidx.test.espresso.device.action.ScreenOrientation.LANDSCAPE
import androidx.test.espresso.device.action.ScreenOrientation.PORTRAIT
import androidx.test.espresso.device.rules.ScreenOrientationRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.masselis.tpmsadvanced.interfaces.screens.Home.Companion.home
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class OrientationChangeTest {

    @get:Rule
    val androidComposeTestRule = createAndroidComposeRule<RootActivity>()

    @get:Rule
    val screenOrientationRule = ScreenOrientationRule(PORTRAIT)

    @Test
    fun settingScreen() {
        androidComposeTestRule.home {
            actionOverflow {
                settings {
                    assertVehicleSettingsDisplayed()
                    onDevice().setScreenOrientation(LANDSCAPE)
                    androidComposeTestRule.waitForIdle()
                    assertVehicleSettingsDisplayed()
                    leave()
                }
            }
        }
    }
}