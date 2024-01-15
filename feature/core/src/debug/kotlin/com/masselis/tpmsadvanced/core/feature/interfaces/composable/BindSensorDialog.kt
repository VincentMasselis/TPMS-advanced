package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.core.androidtest.OneOffComposable
import com.masselis.tpmsadvanced.core.androidtest.OneOffComposable.ExitToken
import com.masselis.tpmsadvanced.core.androidtest.oneOffComposable

context (ComposeTestRule)
@OptIn(ExperimentalTestApi::class)
public class BindSensorDialog : OneOffComposable<BindSensorDialog> by oneOffComposable(
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