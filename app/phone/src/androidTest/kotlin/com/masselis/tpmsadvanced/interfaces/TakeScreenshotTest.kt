package com.masselis.tpmsadvanced.interfaces

import android.app.UiModeManager.MODE_NIGHT_NO
import android.app.UiModeManager.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.MOTORCYCLE
import com.masselis.tpmsadvanced.interfaces.screens.Home
import com.masselis.tpmsadvanced.interfaces.screens.Home.Companion.home
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class TakeScreenshotTest {

    @get:Rule
    val androidComposeTestRule = createAndroidComposeRule<RootActivity>()

    @Test
    fun lightModeScreenshots() {
        androidComposeTestRule.home {
            takeScreenshots(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    @Test
    fun darkModeScreenshots() {
        androidComposeTestRule.home {
            dropdownMenu {
                addVehicle {
                    setVehicleName("Motorcycle")
                    setKind(MOTORCYCLE)
                    add()
                }
            }
            takeScreenshots(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    private fun Home.takeScreenshots(@AppCompatDelegate.NightMode mode: Int) {
        androidComposeTestRule.activityRule.scenario.onActivity {
            AppCompatDelegate.setDefaultNightMode(mode)
        }
        androidComposeTestRule.waitForIdle()
        val prefix = when (mode) {
            MODE_NIGHT_NO -> "light_"
            MODE_NIGHT_YES -> "dark_"
            else -> error("Unknown mode sent $mode")
        }
        capture("${prefix}main")
        actionOverflow {
            settings {
                capture("${prefix}settings")
                leave()
            }
        }
        actionOverflow {
            bindingMethod {
                tapBindManually()
                capture("${prefix}binding_method")
                goBack()
            }
        }
    }

    private fun capture(name: String) = androidComposeTestRule
        .onRoot()
        .captureToImage()
        .asAndroidBitmap()
        .writeToTestStorage(name)
}
