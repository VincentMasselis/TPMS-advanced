package com.masselis.tpmsadvanced.interfaces

import android.app.UiModeManager.MODE_NIGHT_NO
import android.app.UiModeManager.MODE_NIGHT_YES
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.masselis.tpmsadvanced.data.car.model.Vehicle.Kind.MOTORCYCLE
import com.masselis.tpmsadvanced.interfaces.Home.Companion.home
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.milliseconds

@RunWith(AndroidJUnit4::class)
internal class TakeScreenshotTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<RootActivity>()

    private fun capture(name: String) = composeTestRule
        .onRoot()
        .captureToImage()
        .asAndroidBitmap()
        .writeToTestStorage(name)

    @Test
    fun lightModeScreenshots() {
        composeTestRule.home {
            dropdownMenu {
                select("My car")
            }
            takeScreenshots(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    @Test
    fun darkModeScreenshots() {
        composeTestRule.home {
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
        composeTestRule.activityRule.scenario.apply {
            onActivity {
                AppCompatDelegate.setDefaultNightMode(mode)
            }
        }
        composeTestRule.waitForIdle()
        val prefix = when (mode) {
            MODE_NIGHT_NO -> "light_"
            MODE_NIGHT_YES -> "dark_"
            else -> throw IllegalArgumentException()
        }
        capture("${prefix}main")
        settings {
            composeTestRule.waitForIdle()
            capture("${prefix}settings")
            leave()
        }
    }
}
