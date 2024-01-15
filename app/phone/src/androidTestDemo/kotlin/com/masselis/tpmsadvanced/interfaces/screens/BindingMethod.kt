package com.masselis.tpmsadvanced.interfaces.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.core.androidtest.OneOffComposable
import com.masselis.tpmsadvanced.core.androidtest.OneOffComposable.ExitToken
import com.masselis.tpmsadvanced.core.androidtest.OneOffComposable.Instructions
import com.masselis.tpmsadvanced.core.androidtest.oneOffComposable
import com.masselis.tpmsadvanced.core.androidtest.process
import com.masselis.tpmsadvanced.interfaces.composable.ChooseBindingMethodTags
import com.masselis.tpmsadvanced.interfaces.composable.HomeTags
import com.masselis.tpmsadvanced.unlocated.interfaces.ui.UnlocatedSensorsList

context (ComposeTestRule)
@OptIn(ExperimentalTestApi::class)
internal class BindingMethod : OneOffComposable<BindingMethod> by oneOffComposable(
    { waitUntilExactlyOneExists(hasTestTag(ChooseBindingMethodTags.root)) },
    { waitUntilDoesNotExist(hasTestTag(ChooseBindingMethodTags.root)) }
) {
    private val backButtonNode
        get() = onNodeWithTag(HomeTags.backButton)

    private val scanQrCodeRadioEntryNode
        get() = onNodeWithTag(ChooseBindingMethodTags.scanQrCodeRadioEntry)

    private val bindManuallyRadioEntryNode
        get() = onNodeWithTag(ChooseBindingMethodTags.bindManuallyRadioEntry)

    private val goNextButtonNode
        get() = onNodeWithTag(ChooseBindingMethodTags.goNextButton)

    private val unlocatedSensorsListTest = UnlocatedSensorsList()

    fun goBack(): ExitToken<BindingMethod> {
        backButtonNode.performClick()
        return exitToken
    }

    fun tapQrCode() {
        scanQrCodeRadioEntryNode.performClick()
    }

    fun tapBindManually() {
        bindManuallyRadioEntryNode.performClick()
    }

    fun assertNextButtonHidden() {
        goNextButtonNode.assertDoesNotExist()
    }

    fun tapGoToNextButton(
        instructions: Instructions<UnlocatedSensorsList>
    ): ExitToken<BindingMethod> {
        goNextButtonNode.performClick()
        unlocatedSensorsListTest.process(instructions)
        return exitToken
    }
}
