@file:Suppress("MatchingDeclarationName")

package com.masselis.tpmsadvanced.interfaces.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.BindSensorTags
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags.dropdownEntryAddVehicle
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.interfaces.composable.HomeTags
import com.masselis.tpmsadvanced.interfaces.tools.isDisplayed


context (ComposeTestRule)
@OptIn(ExperimentalTestApi::class)
internal class Home private constructor() {

    private val carListDropdownMenu get() = onNodeWithTag(CurrentVehicleDropdownTags.dropdownMenu)
    private val actionOverflowButton get() = onNodeWithTag(HomeTags.Actions.overflow)

    fun dropdownMenu(block: DropdownMenu.() -> Unit) {
        carListDropdownMenu.performClick()
        DropdownMenu().block()
        waitForIdle()
        onNodeWithTag(dropdownEntryAddVehicle).assertDoesNotExist()
    }

    private fun bindSensorButton(location: Vehicle.Kind.Location): SemanticsNodeInteraction {
        waitForIdle()
        return onNodeWithTag(BindSensorTags.Button.tag(location))
    }

    fun assertBindSensorButtonVisible(location: Vehicle.Kind.Location) =
        bindSensorButton(location).assertIsDisplayed()

    fun assertBindSensorButtonHidden(location: Vehicle.Kind.Location) =
        bindSensorButton(location).assertIsNotDisplayed()

    fun waitBindSensorButtonHidden(location: Vehicle.Kind.Location) =
        waitUntil { bindSensorButton(location).isDisplayed().not() }

    fun bindSensorDialog(location: Vehicle.Kind.Location, block: BindSensorDialog.() -> Unit) {
        bindSensorButton(location).performClick()
        BindSensorDialog().block()
        waitForIdle()
        onNodeWithTag(BindSensorTags.Dialog.addToFavoritesTag)
            .assertDoesNotExist()
    }

    fun actionOverflow(block: OverflowMenu.() -> Unit) {
        actionOverflowButton.performClick()
        OverflowMenu().block()
        waitUntilDoesNotExist(hasTestTag(HomeTags.Actions.Overflow.name))
    }

    companion object {
        @Suppress("MemberNameEqualsClassName")
        internal fun ComposeTestRule.home(block: Home.() -> Unit) {
            waitForIdle()
            Home().block()
        }
    }
}
