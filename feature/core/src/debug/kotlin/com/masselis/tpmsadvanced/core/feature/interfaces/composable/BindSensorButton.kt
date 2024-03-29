package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.core.androidtest.EnterComposable
import com.masselis.tpmsadvanced.core.androidtest.EnterExitComposable.Instructions
import com.masselis.tpmsadvanced.core.androidtest.isDisplayed
import com.masselis.tpmsadvanced.core.androidtest.onEnter
import com.masselis.tpmsadvanced.core.androidtest.process
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle

context (ComposeTestRule)
@OptIn(ExperimentalTestApi::class)
public class BindSensorButton(
    private val location: Vehicle.Kind.Location,
) : EnterComposable<BindSensorButton> by onEnter(
    { waitUntilExactlyOneExists(hasTestTag(BindSensorTags.Button.tag(location))) }
) {

    private val bindSensorDialog = BindSensorDialog()

    private val button
        get() = onNodeWithTag(BindSensorTags.Button.tag(location))

    public fun assertIsDisplayed() {
        button.assertIsDisplayed()
    }

    public fun assertIsNotDisplayed() {
        button.assertIsNotDisplayed()
    }

    public fun waitUntilIsNotDisplayed() {
        waitUntil { button.isDisplayed().not() }
    }

    public fun tap(instructions: Instructions<BindSensorDialog>) {
        button.performClick()
        bindSensorDialog.process(instructions)
        waitForIdle()
    }
}