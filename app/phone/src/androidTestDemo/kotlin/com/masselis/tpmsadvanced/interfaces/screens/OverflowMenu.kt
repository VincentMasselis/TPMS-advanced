package com.masselis.tpmsadvanced.interfaces.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.core.androidtest.ExitToken
import com.masselis.tpmsadvanced.core.androidtest.Screen
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.Settings
import com.masselis.tpmsadvanced.interfaces.composable.HomeTags
import com.masselis.tpmsadvanced.interfaces.composable.SettingsTag

context(ComposeTestRule)
@OptIn(ExperimentalTestApi::class)
internal class OverflowMenu(block: OverflowMenu.() -> ExitToken<OverflowMenu>) :
    Screen<OverflowMenu>(block) {
    private val settings
        get() = onNodeWithTag(HomeTags.Actions.Overflow.settings)

    private val bindingMethod
        get() = onNodeWithTag(HomeTags.Actions.Overflow.bindingMethod)

    init {
        runBlock()
    }

    fun settings(block: Settings.() -> ExitToken<Settings>): ExitToken<OverflowMenu> {
        settings.performClick()
        Settings(HomeTags.backButton, SettingsTag.vehicle, block)
        waitUntilExactlyOneExists(hasTestTag(HomeTags.Actions.overflow))
        return exitToken
    }

    fun bindingMethod(block: BindingMethod.() -> ExitToken<BindingMethod>): ExitToken<OverflowMenu> {
        bindingMethod.performClick()
        BindingMethod(block)
        waitUntilExactlyOneExists(hasTestTag(HomeTags.Actions.overflow))
        return exitToken
    }
}
