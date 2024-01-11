package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.core.androidtest.ExitToken
import com.masselis.tpmsadvanced.core.androidtest.Screen

context (ComposeTestRule)
@OptIn(ExperimentalTestApi::class)
public class BindSensorDialog(block: BindSensorDialog.() -> ExitToken<BindSensorDialog>) :
    Screen<BindSensorDialog>(block) {
    private val addToFavoritesButton
        get() = onNodeWithTag(BindSensorTags.Dialog.addToFavoritesButton)
    private val cancelButton
        get() = onNodeWithTag(BindSensorTags.Dialog.cancelButton)

    init {
        waitUntilExactlyOneExists(hasTestTag(BindSensorTags.Dialog.addToFavoritesButton))
        runBlock()
    }

    public fun addToFavorites(): ExitToken<BindSensorDialog> {
        addToFavoritesButton.performClick()
        return exitToken
    }

    public fun cancel(): ExitToken<BindSensorDialog> {
        cancelButton.performClick()
        return exitToken
    }
}