package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.core.androidtest.ExitToken
import com.masselis.tpmsadvanced.core.androidtest.Screen
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.DeleteVehicleButtonTags

context (ComposeTestRule)
@OptIn(ExperimentalTestApi::class)
public class DeleteVehicleDialog(block: DeleteVehicleDialog.() -> ExitToken<DeleteVehicleDialog>) :
    Screen<DeleteVehicleDialog>(block) {
    private val deleteButton
        get() = onNodeWithTag(DeleteVehicleButtonTags.Dialog.delete)
    private val cancelButton
        get() = onNodeWithTag(DeleteVehicleButtonTags.Dialog.cancel)

    init {
        waitUntilExactlyOneExists(hasTestTag(DeleteVehicleButtonTags.Dialog.delete))
        runBlock()
    }

    public fun delete(): ExitToken<DeleteVehicleDialog> {
        deleteButton.performClick()
        return exitToken
    }

    public fun cancel(): ExitToken<DeleteVehicleDialog> {
        cancelButton.performClick()
        return exitToken
    }
}