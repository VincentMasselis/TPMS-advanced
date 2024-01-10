package com.masselis.tpmsadvanced.interfaces.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.DeleteVehicleButtonTags

context (ComposeTestRule)
@OptIn(ExperimentalTestApi::class)
internal class DeleteVehicleDialog {
    private val deleteButton
        get() = onNodeWithTag(DeleteVehicleButtonTags.Dialog.delete)
    private val cancelButton
        get() = onNodeWithTag(DeleteVehicleButtonTags.Dialog.cancel)

    init {
        waitUntilExactlyOneExists(hasTestTag(DeleteVehicleButtonTags.Dialog.delete))
    }

    fun delete() = deleteButton.performClick()

    fun cancel() = cancelButton.performClick()
}