package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.masselis.tpmsadvanced.core.androidtest.ExitToken
import com.masselis.tpmsadvanced.core.androidtest.Screen
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle

context (ComposeTestRule)
@OptIn(ExperimentalTestApi::class)
public class AddVehicle(block: AddVehicle.() -> ExitToken<AddVehicle>) :
    Screen<AddVehicle>(block) {
    private val textField
        get() = onNodeWithTag(CurrentVehicleDropdownTags.AddVehicle.textField)
    private val addButton
        get() = onNodeWithTag(CurrentVehicleDropdownTags.AddVehicle.addButton)
    private val cancelButton
        get() = onNodeWithTag(CurrentVehicleDropdownTags.AddVehicle.cancelButton)

    init {
        waitUntilExactlyOneExists(hasTestTag(CurrentVehicleDropdownTags.AddVehicle.addButton))
        runBlock()
    }

    public fun setVehicleName(name: String) {
        textField.performTextInput(name)
    }

    public fun setKind(kind: Vehicle.Kind) {
        onNodeWithTag(CurrentVehicleDropdownTags.AddVehicle.kindRadio(kind))
            .performClick()
    }

    public fun add(): ExitToken<AddVehicle> {
        addButton.performClick()
        return exitToken
    }

    public fun cancel(): ExitToken<AddVehicle> {
        cancelButton.performClick()
        return exitToken
    }
}