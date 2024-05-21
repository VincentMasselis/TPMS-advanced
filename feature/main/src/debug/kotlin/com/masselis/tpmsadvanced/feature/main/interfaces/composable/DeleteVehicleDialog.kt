package com.masselis.tpmsadvanced.feature.main.interfaces.composable

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.core.androidtest.EnterExitComposable
import com.masselis.tpmsadvanced.core.androidtest.EnterExitComposable.ExitToken
import com.masselis.tpmsadvanced.core.androidtest.onEnterAndOnExit

context (ComposeTestRule)
@OptIn(ExperimentalTestApi::class)
public class DeleteVehicleDialog : EnterExitComposable<DeleteVehicleDialog> by onEnterAndOnExit(
    { waitUntilExactlyOneExists(hasTestTag(DeleteVehicleButtonTags.Dialog.root)) },
    { waitUntilDoesNotExist(hasTestTag(DeleteVehicleButtonTags.Dialog.root)) },
) {
    private val deleteButton
        get() = onNodeWithTag(DeleteVehicleButtonTags.Dialog.delete)
    private val cancelButton
        get() = onNodeWithTag(DeleteVehicleButtonTags.Dialog.cancel)

    public fun delete(): ExitToken<DeleteVehicleDialog> {
        deleteButton.performClick()
        return exitToken
    }

    public fun cancel(): ExitToken<DeleteVehicleDialog> {
        cancelButton.performClick()
        return exitToken
    }
}