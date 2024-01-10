package com.masselis.tpmsadvanced.interfaces.screens

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
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.unlocated.interfaces.ui.BindDialogTags
import com.masselis.tpmsadvanced.unlocated.interfaces.ui.VehicleTyresTags

context (ComposeTestRule)
@OptIn(ExperimentalTestApi::class)
internal class BindDialog {
    private val cancelButton
        get() = onNodeWithTag(BindDialogTags.cancelButton)

    private val bindButton
        get() = onNodeWithTag(BindDialogTags.bindButton)

    private fun location(location: Vehicle.Kind.Location) =
        onAllNodesWithTag(VehicleTyresTags.tyreLocation(location))
            .filterToOne(hasAnyAncestor(isDialog()))

    init {
        waitUntilExactlyOneExists(hasTestTag(BindDialogTags.cancelButton))
    }

    fun assertBindButtonIsNotEnabled() {
        bindButton.assertIsNotEnabled()
    }

    fun tapCancel() {
        cancelButton.performClick()
    }

    fun tapLocation(location: Vehicle.Kind.Location) {
        location(location).performClick()
    }

    fun tapBindButton() {
        bindButton.performClick()
    }
}