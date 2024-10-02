package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.core.androidtest.EnterExitComposable
import com.masselis.tpmsadvanced.core.androidtest.EnterExitComposable.ExitToken
import com.masselis.tpmsadvanced.core.androidtest.EnterExitComposable.Instructions
import com.masselis.tpmsadvanced.core.androidtest.onEnterAndOnExit
import com.masselis.tpmsadvanced.core.androidtest.process
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.CurrentVehicleDropdownTags.currentVehicleTest
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.CurrentVehicleDropdownTags.dropdownEntry
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.CurrentVehicleDropdownTags.dropdownEntryAddVehicle

@OptIn(ExperimentalTestApi::class)
public class DropdownMenu private constructor(
    private val containerTag: String,
    composeTestRule: ComposeTestRule,
) : ComposeTestRule by composeTestRule, EnterExitComposable<DropdownMenu> by onEnterAndOnExit({
    composeTestRule.waitUntilExactlyOneExists(hasTestTag(dropdownEntryAddVehicle))
},
    { composeTestRule.waitUntilDoesNotExist(hasTestTag(dropdownEntryAddVehicle)) }) {

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

    public fun assertCurrentVehicle(vehicleName: String) {
        onNodeWithTag(currentVehicleTest).assertTextEquals(vehicleName)
    }

    public fun select(vehicleName: String): ExitToken<DropdownMenu> {
        entry(vehicleName).performClick()
        return exitToken
    }

    public fun close(): ExitToken<DropdownMenu> {
        carListDropdownMenu.performClick()
        return exitToken
    }

    public companion object {
        context(ComposeTestRule)
        public operator fun invoke(containerTag: String): DropdownMenu =
            DropdownMenu(containerTag, this@ComposeTestRule)
    }
}
