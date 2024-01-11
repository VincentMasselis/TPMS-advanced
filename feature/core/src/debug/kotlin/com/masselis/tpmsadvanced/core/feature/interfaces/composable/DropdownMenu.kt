package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.core.androidtest.ExitToken
import com.masselis.tpmsadvanced.core.androidtest.Screen
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags.dropdownEntry
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags.dropdownEntryAddVehicle

context (ComposeTestRule)
@OptIn(ExperimentalTestApi::class)
public class DropdownMenu(
    private val containerTag: String,
    block: DropdownMenu.() -> ExitToken<DropdownMenu>
) :
    Screen<DropdownMenu>(block) {

    private val addVehicle
        get() = onNodeWithTag(dropdownEntryAddVehicle)
    private val carListDropdownMenu
        get() = onNodeWithTag(containerTag)

    init {
        waitUntilExactlyOneExists(hasTestTag(dropdownEntryAddVehicle))
        runBlock()
    }

    private fun entry(vehicleName: String) = onNodeWithTag(dropdownEntry(vehicleName))

    public fun addVehicle(block: AddVehicle.() -> ExitToken<AddVehicle>): ExitToken<DropdownMenu> {
        addVehicle.performClick()
        AddVehicle(block)
        waitForIdle()
        addVehicle.assertDoesNotExist()
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
