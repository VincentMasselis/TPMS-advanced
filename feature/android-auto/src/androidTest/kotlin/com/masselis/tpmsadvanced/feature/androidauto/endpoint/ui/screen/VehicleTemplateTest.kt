package com.masselis.tpmsadvanced.feature.androidauto.endpoint.ui.screen

import androidx.car.app.Screen
import androidx.car.app.model.GridItem
import androidx.car.app.model.Template
import androidx.car.app.testing.TestCarContext
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.masselis.tpmsadvanced.core.common.Fraction
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit.BAR
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit.CELSIUS
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location.Wheel
import com.masselis.tpmsadvanced.feature.androidauto.endpoint.ui.viewmodel.TabScreenViewModel.State
import com.masselis.tpmsadvanced.feature.main.usecase.TyreIconStateFlow
import com.masselis.tpmsadvanced.feature.main.usecase.TyreStatsStateFlow
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale
import java.util.Locale.US
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

// Runs on a real device/emulator rather than as a local Robolectric test because
// TyreGridItem's icon tinting reads core:ui's LightColors/DarkColors, which are backed by a
// process-wide appContext only ever populated by App Startup running this module's own
// androidTest-only com.masselis.tpmsadvanced.ioc.AppGraph
@RunWith(AndroidJUnit4::class)
internal class VehicleTemplateTest {

    private lateinit var screen: Screen
    private lateinit var defaultLocale: Locale

    // androidx.car.app.CarContext's constructor registers a LifecycleObserver, which enforces
    // being called on the main thread, unlike the instrumentation test thread this class runs on.
    private inline fun <reified T> runOnMain(crossinline block: () -> T): T {
        var result: T? = null
        getInstrumentation().runOnMainSync { result = block() }
        @Suppress("UNCHECKED_CAST")
        return result as T
    }

    @Before
    fun setup() {
        // Pressure/Temperature.string() formats with the device's default locale (e.g. "3,2b" on
        // a comma-decimal locale), pin it so the assertions below are deterministic.
        defaultLocale = Locale.getDefault()
        Locale.setDefault(US)
        screen = runOnMain {
            TestCarContext
                .createCarContext(ApplicationProvider.getApplicationContext())
                .let { carContext ->
                    object : Screen(carContext) {
                        override fun onGetTemplate(): Template = error("Not exercised by this test")
                    }
                }
        }
    }

    private fun singleTyreGridItem(
        iconState: TyreIconStateFlow.State,
        statsState: TyreStatsStateFlow.State,
    ) = Vehicle.Kind.CAR.locations
        .associateWith { location ->
            if (location == Wheel(FRONT_LEFT)) iconState to statsState
            else TyreIconStateFlow.State.NotDetected to TyreStatsStateFlow.State.NotDetected
        }
        .let { tyres -> State.Tabs.Tab.Displayed(mockVehicle(), tyres) }
        .let { tab -> runOnMain { context(screen) { VehicleTemplate(tab) } } }
        .singleList!!
        .items
        .also { items -> assertEquals(4, items.size) }
        .map { item -> assertIs<GridItem>(item); item }
        .first { item -> item.title.toString() == "Front left" }

    @Test
    fun alertingTyreShowsItsLocationAndFormattedPressureWithTemperature() {
        singleTyreGridItem(
            TyreIconStateFlow.State.Alerting,
            TyreStatsStateFlow.State.Alerting(0.0, 3.2f.bar, BAR, 60f.celsius, CELSIUS),
        ).also { item ->
            assertEquals("Front left", item.title.toString())
            assertEquals("3.2b  60°C", item.text.toString())
            assertNotNull(item.image)
        }
    }

    @Test
    fun normalTyreShowsItsLocationAndFormattedPressureWithTemperature() {
        singleTyreGridItem(
            TyreIconStateFlow.State.Normal.BlueToGreen(Fraction(0.5f)),
            TyreStatsStateFlow.State.Normal(0.0, 2.4f.bar, BAR, 22f.celsius, CELSIUS),
        ).also { item ->
            assertEquals("Front left", item.title.toString())
            assertEquals("2.4b  22°C", item.text.toString())
            assertNotNull(item.image)
        }
    }

    @Test
    fun notDetectedTyreShowsItsLocationAndAPlaceholderText() {
        singleTyreGridItem(
            TyreIconStateFlow.State.NotDetected,
            TyreStatsStateFlow.State.NotDetected,
        ).also { item ->
            assertEquals("Front left", item.title.toString())
            assertEquals("-.-", item.text.toString())
            assertNotNull(item.image)
        }
    }

    @Test
    fun detectionIssueTyreShowsItsLocationAndAPlaceholderText() {
        singleTyreGridItem(
            TyreIconStateFlow.State.DetectionIssue,
            TyreStatsStateFlow.State.NotDetected,
        ).also { item ->
            assertEquals("Front left", item.title.toString())
            assertEquals("-.-", item.text.toString())
            assertNotNull(item.image)
        }
    }

    @Test
    fun oneGridItemIsRenderedPerTyreLocation() {
        Vehicle.Kind.CAR.locations
            .associateWith { TyreIconStateFlow.State.Alerting to TyreStatsStateFlow.State.NotDetected }
            .let { tyres -> State.Tabs.Tab.Displayed(mockVehicle(), tyres) }
            .let { tab -> runOnMain { context(screen) { VehicleTemplate(tab) } } }
            .singleList!!
            .items
            .also { items -> assertEquals(4, items.size) }
            .map { assertIs<GridItem>(it) }
            .map { it.title.toString() }
            .toSet()
            .also { titles ->
                assertEquals(setOf("Front left", "Front right", "Rear left", "Rear right"), titles)
            }
    }

    @After
    fun tearDown() {
        Locale.setDefault(defaultLocale)
    }
}
