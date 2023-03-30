package com.masselis.tpmsadvanced.interfaces

import android.app.UiModeManager.MODE_NIGHT_NO
import android.app.UiModeManager.MODE_NIGHT_YES
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
    fun setup() {
        composeTestRule.home {
            dropdownMenu {
                if (exists("Motorcycle"))
                    close()
                else {
                    addVehicle {
                        setVehicleName("Motorcycle")
                        setKind(MOTORCYCLE)
                        add()
                    }
                }
            }
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
        composeTestRule.waitForIdle()
        val prefix = when (mode) {
            MODE_NIGHT_NO -> "light_"
            MODE_NIGHT_YES -> "dark_"
            else -> throw IllegalArgumentException()
        }
        delay(500.milliseconds)
        capture("${prefix}main")
        composeTestRule.home {
            settings {
                composeTestRule.waitForIdle()
                capture("${prefix}settings")
                leave()
            }
        }
    }

    private fun chooseVehicle(name: String) {
        composeTestRule.home {
            dropdownMenu {
                select(name)
            }
        }
    }
}
