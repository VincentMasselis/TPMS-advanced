package com.masselis.tpmsadvanced.interfaces.screens

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.interfaces.composable.ChooseBindingMethodTags
import com.masselis.tpmsadvanced.interfaces.composable.HomeTags

context (ComposeTestRule)
internal class BindingMethod {
    private val backButton
        get() = onNodeWithTag(HomeTags.backButton)

    private val scanQrCode
        get() = onNodeWithTag(ChooseBindingMethodTags.scanQrCode)

    private val bindManually
        get() = onNodeWithTag(ChooseBindingMethodTags.bindManually)

    private val goNextButton
        get() = onNodeWithTag(ChooseBindingMethodTags.goNextButton)

    fun goBack() {
        backButton.performClick()
    }

    fun tapQrCode() {
        scanQrCode.performClick()
    }

    fun tapBindManually() {
        bindManually.performClick()
    }

    fun assertNextButtonHidden() {
        goNextButton.assertDoesNotExist()
    }

    fun tapGoToNextButton(block: UnlocatedSensorsList.() -> Unit) {
        goNextButton.performClick()
        UnlocatedSensorsList().block()
    }
}