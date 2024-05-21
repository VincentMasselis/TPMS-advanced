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
public class BindSensorDialog : EnterExitComposable<BindSensorDialog> by onEnterAndOnExit(
    { waitUntilExactlyOneExists(hasTestTag(BindSensorTags.Dialog.root)) },
    { waitUntilDoesNotExist(hasTestTag(BindSensorTags.Dialog.root)) },
) {

    private val addToFavoritesButtonNode
        get() = onNodeWithTag(BindSensorTags.Dialog.addToFavoritesButton)
    private val cancelButtonNode
        get() = onNodeWithTag(BindSensorTags.Dialog.cancelButton)

    public fun addToFavorites(): ExitToken<BindSensorDialog> {
        addToFavoritesButtonNode.performClick()
        return exitToken
    }

    public fun cancel(): ExitToken<BindSensorDialog> {
        cancelButtonNode.performClick()
        return exitToken
    }
}