package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.masselis.tpmsadvanced.core.androidtest.OneOffComposable
import com.masselis.tpmsadvanced.core.androidtest.OneOffComposable.ExitToken
import com.masselis.tpmsadvanced.core.androidtest.oneOffComposable
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle

context (ComposeTestRule)
@OptIn(ExperimentalTestApi::class)
public class AddVehicle : OneOffComposable<AddVehicle> by oneOffComposable(
    { waitUntilExactlyOneExists(hasTestTag(CurrentVehicleDropdownTags.AddVehicle.root)) },
    { waitUntilDoesNotExist(hasTestTag(CurrentVehicleDropdownTags.AddVehicle.root)) }
) {
    private val textFieldNode
        get() = onNodeWithTag(CurrentVehicleDropdownTags.AddVehicle.textField)
    private val addButtonNode
        get() = onNodeWithTag(CurrentVehicleDropdownTags.AddVehicle.addButton)
    private val cancelButtonNode
        get() = onNodeWithTag(CurrentVehicleDropdownTags.AddVehicle.cancelButton)

    public fun setVehicleName(name: String) {
        textFieldNode.performTextInput(name)
    }

    public fun setKind(kind: Vehicle.Kind) {
        onNodeWithTag(CurrentVehicleDropdownTags.AddVehicle.kindRadio(kind))
            .performClick()
    }

    public fun add(): ExitToken<AddVehicle> {
        addButtonNode.performClick()
        return exitToken
    }

    public fun cancel(): ExitToken<AddVehicle> {
        cancelButtonNode.performClick()
        return exitToken
    }
}