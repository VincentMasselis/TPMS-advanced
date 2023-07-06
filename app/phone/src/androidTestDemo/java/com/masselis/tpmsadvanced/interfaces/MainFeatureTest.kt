package com.masselis.tpmsadvanced.interfaces

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.masselis.tpmsadvanced.data.car.model.Vehicle.Kind.CAR
import com.masselis.tpmsadvanced.data.car.model.Vehicle.Kind.Location
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
            actionOverflow {
                settings {
                    assert(isVehicleDeleteEnabled().not())
                    leave()
                }
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
            assert(isBindSensorAvailable(Location.Wheel(FRONT_LEFT)))
            bindSensorDialog(Location.Wheel(FRONT_LEFT)) {
                cancel()
            }
            bindSensorDialog(Location.Wheel(FRONT_LEFT)) {
                addToFavorites()
            }
            composeTestRule.waitUntil { isBindSensorAvailable(Location.Wheel(FRONT_LEFT)).not() }
            actionOverflow {
                settings {
                    composeTestRule.waitUntil { isClearFavouritesEnabled() }
                    clearFavourites()
                    composeTestRule.waitUntil { isClearFavouritesEnabled().not() }
                    leave()
                }
            }
            dropdownMenu {
                select("My car")
            }
            actionOverflow {
                settings {
                    deleteVehicle {
                        cancel()
                    }
                    deleteVehicle {
                        delete()
                    }
                }
            }
            dropdownMenu {
                composeTestRule.waitUntil { exists("My car").not() }
                close()
            }
        }
    }
}
