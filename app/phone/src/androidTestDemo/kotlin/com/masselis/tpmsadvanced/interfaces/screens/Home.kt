@file:Suppress("MatchingDeclarationName")

package com.masselis.tpmsadvanced.interfaces.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.core.androidtest.ExitToken
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.BindSensorButton
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.BindSensorDialog
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.DropdownMenu
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.interfaces.composable.HomeTags


context (ComposeTestRule)
@OptIn(ExperimentalTestApi::class)
internal class Home private constructor() {

    private val carListDropdownMenu get() = onNodeWithTag(HomeTags.carListDropdownMenu)
    private val actionOverflowButton get() = onNodeWithTag(HomeTags.Actions.overflow)

    fun dropdownMenu(block: DropdownMenu.() -> ExitToken<DropdownMenu>) {
        carListDropdownMenu.performClick()
        DropdownMenu(HomeTags.carListDropdownMenu, block)
        waitForIdle()
    }

    fun bindSensorButton(location: Vehicle.Kind.Location, block: BindSensorButton.() -> Unit) =
        BindSensorButton(location, block)

    fun actionOverflow(block: OverflowMenu.() -> ExitToken<OverflowMenu>) {
        actionOverflowButton.performClick()
        OverflowMenu(block)
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
