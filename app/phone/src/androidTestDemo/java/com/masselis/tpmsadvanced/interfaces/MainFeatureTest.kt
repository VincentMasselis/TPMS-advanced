package com.masselis.tpmsadvanced.interfaces

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.masselis.tpmsadvanced.core.feature.model.ManySensor
import com.masselis.tpmsadvanced.data.car.model.Vehicle.Kind.CAR
import com.masselis.tpmsadvanced.data.car.model.Vehicle.Kind.MOTORCYCLE
import com.masselis.tpmsadvanced.data.record.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.interfaces.Home.Companion.home
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class MainFeatureTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<RootActivity>()

    @Suppress("LongMethod")
    @Test
    fun mainFeatures() {
        composeTestRule.home {
            settings {
                assert(isVehicleDeleteEnabled().not())
                leave()
            }
            dropdownMenu {
                addVehicle {
                    cancel()
                }
            }
            dropdownMenu {
                addVehicle {
                    setVehicleName("Car")
                    setKind(CAR)
                    add()
                }
            }
            dropdownMenu {
                addVehicle {
                    setVehicleName("Motorcycle")
                    setKind(MOTORCYCLE)
                    add()
                }
            }
            dropdownMenu {
                assert(exists("Car") && exists("Motorcycle"))
                close()
            }
            dropdownMenu {
                select("Car")
            }
            assert(isBindSensorAvailable(ManySensor.Located(FRONT_LEFT)))
            bindSensorDialog(ManySensor.Located(FRONT_LEFT)) {
                cancel()
            }
            bindSensorDialog(ManySensor.Located(FRONT_LEFT)) {
                addToFavorites()
            }
            composeTestRule.waitUntil { isBindSensorAvailable(ManySensor.Located(FRONT_LEFT)).not() }
            settings {
                composeTestRule.waitUntil { isClearFavouritesEnabled() }
                clearFavourites()
                composeTestRule.waitUntil { isClearFavouritesEnabled().not() }
                leave()
            }
            dropdownMenu {
                select("My car")
            }
            settings {
                deleteVehicle {
                    cancel()
                }
                deleteVehicle {
                    delete()
                }
            }
            dropdownMenu {
                assert(exists("My car").not())
                close()
            }
        }
    }
}
