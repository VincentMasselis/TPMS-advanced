package com.masselis.tpmsadvanced.unlocated.interfaces.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.core.androidtest.ExitToken
import com.masselis.tpmsadvanced.core.androidtest.Screen
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.unlocated.interfaces.ui.BindDialogTags
import com.masselis.tpmsadvanced.unlocated.interfaces.ui.VehicleTyresTags

context (ComposeTestRule)
@OptIn(ExperimentalTestApi::class)
public class BindDialog(block: BindDialog.() -> ExitToken<BindDialog>) :
    Screen<BindDialog>(block) {
    private val cancelButton
        get() = onNodeWithTag(BindDialogTags.cancelButton)

    private val bindButton
        get() = onNodeWithTag(BindDialogTags.bindButton)

    private fun location(location: Vehicle.Kind.Location) =
        onAllNodesWithTag(VehicleTyresTags.tyreLocation(location))
            .filterToOne(hasAnyAncestor(hasTestTag(BindDialogTags.bindDialog)))

    init {
        waitUntilExactlyOneExists(hasTestTag(BindDialogTags.cancelButton))
        runBlock()
    }

    public fun assertBindButtonIsNotEnabled() {
        bindButton.assertIsNotEnabled()
    }

    public fun tapCancel(): ExitToken<BindDialog> {
        cancelButton.performClick()
        return exitToken
    }

    public fun tapLocation(location: Vehicle.Kind.Location) {
        location(location).performClick()
    }

    public fun tapBindButton(): ExitToken<BindDialog> {
        bindButton.performClick()
        return exitToken
    }
}