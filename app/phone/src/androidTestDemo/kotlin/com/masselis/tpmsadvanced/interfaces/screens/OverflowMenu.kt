package com.masselis.tpmsadvanced.interfaces.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.interfaces.composable.HomeTags

context(ComposeTestRule)
@OptIn(ExperimentalTestApi::class)
internal class OverflowMenu {
    private val settings
        get() = onNodeWithTag(HomeTags.Actions.Overflow.settings)

    private val bindingMethod
        get() = onNodeWithTag(HomeTags.Actions.Overflow.bindingMethod)

    fun settings(block: Settings.() -> Unit) {
        settings.performClick()
        Settings().block()
        waitUntilExactlyOneExists(hasTestTag(HomeTags.Actions.overflow))
    }

    fun bindingMethod(block: BindingMethod.() -> Unit) {
        bindingMethod.performClick()
        BindingMethod().block()
        waitUntilExactlyOneExists(hasTestTag(HomeTags.Actions.overflow))
    }
}
