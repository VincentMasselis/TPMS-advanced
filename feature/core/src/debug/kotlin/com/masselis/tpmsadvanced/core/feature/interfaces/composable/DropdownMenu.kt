package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.core.androidtest.OneOffComposable
import com.masselis.tpmsadvanced.core.androidtest.OneOffComposable.ExitToken
import com.masselis.tpmsadvanced.core.androidtest.OneOffComposable.Instructions
import com.masselis.tpmsadvanced.core.androidtest.process
import com.masselis.tpmsadvanced.core.androidtest.oneOffComposable
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags.dropdownEntry
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags.dropdownEntryAddVehicle

context (ComposeTestRule)
@OptIn(ExperimentalTestApi::class)
public class DropdownMenu(
    private val containerTag: String,
) : OneOffComposable<DropdownMenu> by oneOffComposable(
    { waitUntilExactlyOneExists(hasTestTag(dropdownEntryAddVehicle)) },
    { waitUntilDoesNotExist(hasTestTag(dropdownEntryAddVehicle)) }
) {

    private val addVehicleNode
        get() = onNodeWithTag(dropdownEntryAddVehicle)
    private val carListDropdownMenu
        get() = onNodeWithTag(containerTag)

    private val addVehicleTest = AddVehicle()

    private fun entry(vehicleName: String) = onNodeWithTag(dropdownEntry(vehicleName))

    public fun addVehicle(instructions: Instructions<AddVehicle>): ExitToken<DropdownMenu> {
        addVehicleNode.performClick()
        addVehicleTest.process(instructions)
        return exitToken
    }

    public fun assertVehicleExists(vehicleName: String) {
        entry(vehicleName).assertExists()
    }

    public fun assertVehicleDoesNotExists(vehicleName: String) {
        entry(vehicleName).assertDoesNotExist()
    }

    public fun select(vehicleName: String): ExitToken<DropdownMenu> {
        entry(vehicleName).performClick()
        return exitToken
    }

    public fun close(): ExitToken<DropdownMenu> {
        carListDropdownMenu.performClick()
        return exitToken
    }
}
