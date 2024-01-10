package com.masselis.tpmsadvanced.interfaces

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.masselis.tpmsadvanced.interfaces.screens.Home.Companion.home
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class OrientationChangeTest {

    @get:Rule
    val androidComposeTestRule = createAndroidComposeRule<RootActivity>()

    @Test
    fun settingScreen() {
        androidComposeTestRule.home {
            actionOverflow {
                settings {
                    assertVehicleSettingsDisplayed()
                    androidComposeTestRule.activity.requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE
                    assertVehicleSettingsDisplayed()
                    leave()
                }
            }
        }
    }
}