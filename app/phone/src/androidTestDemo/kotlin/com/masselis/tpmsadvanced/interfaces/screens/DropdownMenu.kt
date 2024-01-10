package com.masselis.tpmsadvanced.interfaces.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags.dropdownEntry
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags.dropdownEntryAddVehicle
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags.dropdownMenu

context (ComposeTestRule)
@OptIn(ExperimentalTestApi::class)
internal class DropdownMenu {
    private val addVehicle
        get() = onNodeWithTag(dropdownEntryAddVehicle)
    private val carListDropdownMenu
        get() = onNodeWithTag(dropdownMenu)

    init {
        waitUntilExactlyOneExists(hasTestTag(dropdownEntryAddVehicle))
    }

    private fun Entry(vehicleName: String) = onNodeWithTag(dropdownEntry(vehicleName))

    fun addVehicle(block: AddVehicle.() -> Unit) {
        addVehicle.performClick()
        AddVehicle().block()
        waitForIdle()
        addVehicle.assertDoesNotExist()
    }

    fun assertVehicleExists(vehicleName: String) = Entry(vehicleName).assertExists()

    fun assertVehicleDoesNotExists(vehicleName: String) = Entry(vehicleName).assertDoesNotExist()

    fun select(vehicleName: String) = Entry(vehicleName).performClick()

    fun close() = carListDropdownMenu.performClick()

}