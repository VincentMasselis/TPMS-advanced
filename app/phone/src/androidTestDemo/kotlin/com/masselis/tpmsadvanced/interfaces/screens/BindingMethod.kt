package com.masselis.tpmsadvanced.interfaces.screens

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.core.androidtest.ExitToken
import com.masselis.tpmsadvanced.core.androidtest.Screen
import com.masselis.tpmsadvanced.interfaces.composable.ChooseBindingMethodTags
import com.masselis.tpmsadvanced.interfaces.composable.HomeTags
import com.masselis.tpmsadvanced.unlocated.interfaces.ui.UnlocatedSensorsList

context (ComposeTestRule)
internal class BindingMethod(block: BindingMethod.() -> ExitToken<BindingMethod>) :
    Screen<BindingMethod>(block) {
    private val backButton
        get() = onNodeWithTag(HomeTags.backButton)

    private val scanQrCodeRadioEntry
        get() = onNodeWithTag(ChooseBindingMethodTags.scanQrCodeRadioEntry)

    private val bindManuallyRadioEntry
        get() = onNodeWithTag(ChooseBindingMethodTags.bindManuallyRadioEntry)

    private val goNextButton
        get() = onNodeWithTag(ChooseBindingMethodTags.goNextButton)

    init {
        runBlock()
    }

    fun goBack(): ExitToken<BindingMethod> {
        backButton.performClick()
        return exitToken
    }

    fun tapQrCode() {
        scanQrCodeRadioEntry.performClick()
    }

    fun tapBindManually() {
        bindManuallyRadioEntry.performClick()
    }

    fun assertNextButtonHidden() {
        goNextButton.assertDoesNotExist()
    }

    fun tapGoToNextButton(
        block: UnlocatedSensorsList.() -> ExitToken<UnlocatedSensorsList>
    ): ExitToken<BindingMethod> {
        goNextButton.performClick()
        UnlocatedSensorsList(block)
        return exitToken
    }
}