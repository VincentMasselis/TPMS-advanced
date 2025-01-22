@file:Suppress("MatchingDeclarationName")

package com.masselis.tpmsadvanced.interfaces.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.core.androidtest.EnterComposable
import com.masselis.tpmsadvanced.core.androidtest.EnterExitComposable.Instructions
import com.masselis.tpmsadvanced.core.androidtest.onEnter
import com.masselis.tpmsadvanced.core.androidtest.process
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.BindSensorButton
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.DropdownMenu
import com.masselis.tpmsadvanced.interfaces.composable.HomeTags
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.BindSensorButton.Companion.invoke as BindSensorButton
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.DropdownMenu.Companion.invoke as DropdownMenu
import com.masselis.tpmsadvanced.interfaces.screens.OverflowMenu.Companion.invoke as OverflowMenu


@OptIn(ExperimentalTestApi::class)
internal class Home private constructor(
    composeTestRule: ComposeTestRule
) : ComposeTestRule by composeTestRule,
    EnterComposable<Home> by onEnter(
        { composeTestRule.waitUntilExactlyOneExists(hasTestTag(HomeTags.carListDropdownMenu)) }
    ) {

    private val carListDropdownMenu get() = onNodeWithTag(HomeTags.carListDropdownMenu)
    private val actionOverflowButton get() = onNodeWithTag(HomeTags.Actions.overflow)

    private val dropdownMenuTest = DropdownMenu(HomeTags.carListDropdownMenu)
    private val overflowMenuTest = OverflowMenu()

    fun dropdownMenu(instructions: Instructions<DropdownMenu>) {
        carListDropdownMenu.performClick()
        dropdownMenuTest.process(instructions)
        waitForIdle()
    }

    fun bindSensorButton(
        location: Vehicle.Kind.Location,
        instructions: EnterComposable.Instructions<BindSensorButton>
    ) {
        BindSensorButton(location).process(instructions)
    }

    fun actionOverflow(block: Instructions<OverflowMenu>) {
        actionOverflowButton.performClick()
        overflowMenuTest.process(block)
    }

    companion object {
        @Suppress("MemberNameEqualsClassName")
        internal fun ComposeTestRule.home(block: Home.() -> Unit) {
            waitForIdle()
            Home(this).block()
        }
    }
}
