package com.masselis.tpmsadvanced.interfaces

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.BindSensorButton
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.CAR
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.MOTORCYCLE
import com.masselis.tpmsadvanced.interfaces.screens.Home.Companion.home
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class MainFeatureTest {

    @get:Rule
    val androidComposeTestRule = createAndroidComposeRule<RootActivity>()

    @Suppress("LongMethod")
    @Test
    fun mainFeatures() = androidComposeTestRule.home {
        actionOverflow {
            settings {
                assertVehicleDeleteIsNotEnabled()
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
            assertVehicleExists("Car")
            assertVehicleExists("Motorcycle")
            close()
        }
        dropdownMenu {
            select("Car")
        }
        bindSensorButton(Location.Wheel(FRONT_LEFT)) {
            assertIsDisplayed()
            tap {
                cancel()
            }
            tap {
                addToFavorites()
            }
            assertIsNotDisplayed()
        }
        actionOverflow {
            settings {
                waitClearFavouritesEnabled()
                clearFavourites()
                waitClearFavouritesDisabled()
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
            assertVehicleDoesNotExists("My car")
            select("Car")
        }
        actionOverflow {
            bindingMethod {
                goBack()
            }
        }
        actionOverflow {
            bindingMethod {
                assertNextButtonHidden()
                tapQrCode()
                tapBindManually()
                tapGoToNextButton {
                    tapSensorUnplugged()
                    tapSensor(2) {
                        assertBindButtonIsNotEnabled()
                        tapLocation(Location.Wheel(FRONT_LEFT))
                        tapCancel()
                    }
                    tapSensor(2) {
                        tapLocation(Location.Wheel(FRONT_LEFT))
                        tapBindButton()
                    }
                    tapSensor(4) {
                        tapLocation(Location.Wheel(FRONT_RIGHT))
                        tapBindButton()
                    }
                    tapSensor(6) {
                        tapLocation(Location.Wheel(REAR_LEFT))
                        tapBindButton()
                    }
                    tapSensor(8) {
                        tapLocation(Location.Wheel(REAR_RIGHT))
                        tapBindButton()
                    }
                    assertAllLocationBound(
                        2 to Location.Wheel(FRONT_LEFT),
                        4 to Location.Wheel(FRONT_RIGHT),
                        6 to Location.Wheel(REAR_LEFT),
                        8 to Location.Wheel(REAR_RIGHT),
                    )
                    tapGoBack()
                }
            }
        }
        bindSensorButton(Location.Wheel(FRONT_LEFT)) { assertIsNotDisplayed() }
        bindSensorButton(Location.Wheel(FRONT_RIGHT)) { assertIsNotDisplayed() }
        bindSensorButton(Location.Wheel(REAR_LEFT)) { assertIsNotDisplayed() }
        bindSensorButton(Location.Wheel(REAR_RIGHT)) { assertIsNotDisplayed() }
    }
}
