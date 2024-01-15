package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.core.androidtest.OneOffComposable.Instructions
import com.masselis.tpmsadvanced.core.androidtest.isDisplayed
import com.masselis.tpmsadvanced.core.androidtest.process
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle

context (ComposeTestRule)
public class BindSensorButton(
    private val location: Vehicle.Kind.Location,
) {

    // TODO Inherit from OneOffComposable ?

    private val bindSensorDialog = BindSensorDialog()

    private val button
        get() = onNodeWithTag(BindSensorTags.Button.tag(location))

    init {
        waitForIdle()
    }

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