package com.masselis.tpmsadvanced.unlocated.interfaces.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.core.androidtest.OneOffComposable
import com.masselis.tpmsadvanced.core.androidtest.OneOffComposable.ExitToken
import com.masselis.tpmsadvanced.core.androidtest.oneOffComposable
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle

context (ComposeTestRule)
@OptIn(ExperimentalTestApi::class)
public class BindDialog : OneOffComposable<BindDialog> by oneOffComposable(
    { waitUntilExactlyOneExists(hasTestTag(BindDialogTags.root)) },
    { waitUntilDoesNotExist(hasTestTag(BindDialogTags.root)) },
) {
    private val cancelButtonNode
        get() = onNodeWithTag(BindDialogTags.cancelButton)

    private val bindButtonNode
        get() = onNodeWithTag(BindDialogTags.bindButton)

    private fun locationNode(location: Vehicle.Kind.Location) =
        onAllNodesWithTag(VehicleTyresTags.tyreLocation(location))
            .filterToOne(hasAnyAncestor(hasTestTag(BindDialogTags.root)))

    public fun assertBindButtonIsNotEnabled() {
        bindButtonNode.assertIsNotEnabled()
    }

    public fun tapCancel(): ExitToken<BindDialog> {
        cancelButtonNode.performClick()
        return exitToken
    }

    public fun tapLocation(location: Vehicle.Kind.Location) {
        locationNode(location).performClick()
    }

    public fun tapBindButton(): ExitToken<BindDialog> {
        bindButtonNode.performClick()
        return exitToken
    }
}