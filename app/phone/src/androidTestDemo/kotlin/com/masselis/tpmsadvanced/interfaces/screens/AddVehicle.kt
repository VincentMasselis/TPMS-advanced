package com.masselis.tpmsadvanced.interfaces.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle

context (ComposeTestRule)
@OptIn(ExperimentalTestApi::class)
internal class AddVehicle {
    private val textField
        get() = onNodeWithTag(CurrentVehicleDropdownTags.AddVehicle.textField)
    private val addButton
        get() = onNodeWithTag(CurrentVehicleDropdownTags.AddVehicle.addButton)
    private val cancelButton
        get() = onNodeWithTag(CurrentVehicleDropdownTags.AddVehicle.cancelButton)

    init {
        waitUntilExactlyOneExists(hasTestTag(CurrentVehicleDropdownTags.AddVehicle.addButton))
    }

    fun setVehicleName(name: String) {
        textField.performTextInput(name)
    }

    fun setKind(kind: Vehicle.Kind) {
        onNodeWithTag(CurrentVehicleDropdownTags.AddVehicle.kindRadio(kind))
            .performClick()
    }

    fun add() = addButton.performClick()
    fun cancel() = cancelButton.performClick()
}