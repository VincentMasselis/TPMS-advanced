package com.masselis.tpmsadvanced.feature.androidauto.endpoint.ui.screen

import androidx.car.app.Screen
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.TabTemplate
import androidx.car.app.testing.TestCarContext
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.masselis.tpmsadvanced.core.common.appGraph
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertIs

@ContributesTo(AppScope::class)
internal interface VehicleDatabaseExtractor {
    val vehicleDatabase: VehicleDatabase
}

// The `TabTemplate` layout only appears once 2+ vehicles exist. Each @Test starts from the
// single "My car" vehicle seeded on database creation (Android Test Orchestrator clears app data
// between tests), so these insert extra vehicles into the real on-device database rather than
// mocking TabScreenViewModel's use cases, exercising the same Metro-provided AppGraph as
// VehicleTemplateTest.
internal class TabsScreenTest {

    private lateinit var vehicleDatabase: VehicleDatabase

    @Before
    fun setup() {
        vehicleDatabase = (appGraph as VehicleDatabaseExtractor).vehicleDatabase
    }

    private inline fun <reified T> runOnMain(crossinline block: () -> T): T {
        var result: T? = null
        getInstrumentation().runOnMainSync { result = block() }
        @Suppress("UNCHECKED_CAST")
        return result as T
    }

    private fun buildScreen(): Screen = runOnMain {
        TabsScreen(TestCarContext.createCarContext(getApplicationContext()))
    }

    /**
     * TabScreenViewModel builds its state asynchronously from a coroutine flow, so the first
     * template(s) read off a freshly built screen may still reflect the previous (single-vehicle)
     * state
     */
    private fun awaitTabTemplate(screen: Screen, timeoutMs: Long = 5_000): TabTemplate {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            runOnMain { screen.onGetTemplate() }.let { if (it is TabTemplate) return it }
            Thread.sleep(20)
        }
        error("Timed out waiting for a TabTemplate")
    }

    /**
     * Same idea as awaitTabTemplate, but for the single-vehicle path, where onGetTemplate()
     * returns the vehicle's GridTemplate directly instead of a TabTemplate.
     */
    private fun awaitGridTemplate(screen: Screen, timeoutMs: Long = 5_000): GridTemplate {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            runOnMain { screen.onGetTemplate() }.let { if (it is GridTemplate) return it }
            Thread.sleep(20)
        }
        error("Timed out waiting for a GridTemplate")
    }

    @Test
    fun singleVehicleHidesTheTabsAndRendersItsGridDirectly() {
        val gridTemplate = awaitGridTemplate(buildScreen())

        assertEquals(4, gridTemplate.singleList!!.items.size)
    }

    @Test
    fun twoVehiclesRenderOneTabPerVehicleSortedByName() {
        val myCar = vehicleDatabase.currentVehicle().execute()
        val otherCar = mockVehicle()
        runBlocking { vehicleDatabase.insert(otherCar.uuid, otherCar.kind, otherCar.name, true) }

        val tabTemplate = awaitTabTemplate(buildScreen())

        assertEquals(2, tabTemplate.tabs.size)
        assertEquals(
            listOf(myCar.name, otherCar.name).sorted(),
            tabTemplate.tabs.map { it.title.toString() }.sorted()
        )
        assertEquals(
            listOf(myCar.uuid.toString(), otherCar.uuid.toString()).sorted(),
            tabTemplate.tabs.map { it.contentId }.sorted(),
        )
        // "Other car" was inserted as current, so its tab is the active one and the grid content
        // reflects it, not "My car".
        assertEquals(otherCar.uuid.toString(), tabTemplate.activeTabContentId)
        assertIs<GridTemplate>(tabTemplate.tabContents.template)
    }

    @Test
    fun fivePlusVehiclesCapTabsAtFourWithTheCurrentVehiclePinnedFirst() {
        val myCar = vehicleDatabase.currentVehicle().execute()
        runBlocking {
            listOf("Zulu", "Alpha", "Beta", "Gamma").forEach { name ->
                vehicleDatabase.insert(UUID.randomUUID(), Vehicle.Kind.CAR, name, false)
            }
        }

        val tabTemplate = awaitTabTemplate(buildScreen())

        // "My car" stays current (none of the 4 new vehicles were inserted as current), sorts
        // first, and "Zulu" is the one dropped by the take(4) cap.
        assertEquals(4, tabTemplate.tabs.size)
        assertEquals(
            listOf("My car", "Alpha", "Beta", "Gamma"),
            tabTemplate.tabs.map { it.title.toString() },
        )
        assertEquals(myCar.uuid.toString(), tabTemplate.activeTabContentId)
    }
}
