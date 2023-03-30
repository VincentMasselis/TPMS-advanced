package com.masselis.tpmsadvanced.interfaces

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.masselis.tpmsadvanced.core.feature.model.ManySensor
import com.masselis.tpmsadvanced.data.car.model.Vehicle
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import com.masselis.tpmsadvanced.interfaces.Home.Companion.home
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class MainFeatureTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule(RootActivity::class.java)

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
                    setKind(Vehicle.Kind.CAR)
                    add()
                }
            }
            dropdownMenu {
                addVehicle {
                    setVehicleName("Motorcycle")
                    setKind(Vehicle.Kind.MOTORCYCLE)
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
            assert(isBindSensorAvailable(ManySensor.Located(SensorLocation.FRONT_LEFT)))
            bindSensorDialog(ManySensor.Located(SensorLocation.FRONT_LEFT)) {
                cancel()
            }
            bindSensorDialog(ManySensor.Located(SensorLocation.FRONT_LEFT)) {
                addToFavorites()
            }
            assert(isBindSensorAvailable(ManySensor.Located(SensorLocation.FRONT_LEFT)).not())
            settings {
                assert(isClearFavouritesEnabled())
                clearFavourites()
                assert(isClearFavouritesEnabled().not())
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
