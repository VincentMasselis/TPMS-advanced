package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.core.androidtest.ExitToken
import com.masselis.tpmsadvanced.core.androidtest.Screen
import com.masselis.tpmsadvanced.core.androidtest.isDisplayed
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle

context (ComposeTestRule)
public class BindSensorButton(
    private val location: Vehicle.Kind.Location,
    block: BindSensorButton.() -> Unit
) {

    private val button
        get() = onNodeWithTag(BindSensorTags.Button.tag(location))

    init {
        waitForIdle()
        block()
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

    public fun tap(block: BindSensorDialog.() -> ExitToken<BindSensorDialog>) {
        button.performClick()
        BindSensorDialog(block)
        waitForIdle()
    }
}