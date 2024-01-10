package com.masselis.tpmsadvanced.interfaces.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.BindSensorTags

context (ComposeTestRule)
@OptIn(ExperimentalTestApi::class)
internal class BindSensorDialog {
    private val addToFavoritesButton
        get() = onNodeWithTag(BindSensorTags.Dialog.addToFavoritesTag)
    private val cancelButton
        get() = onNodeWithTag(BindSensorTags.Dialog.cancelTag)

    init {
        waitUntilExactlyOneExists(hasTestTag(BindSensorTags.Dialog.addToFavoritesTag))
    }

    fun addToFavorites() = addToFavoritesButton.performClick()
    fun cancel() = cancelButton.performClick()
}