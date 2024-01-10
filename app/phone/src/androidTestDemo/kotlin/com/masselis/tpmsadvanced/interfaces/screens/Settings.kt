package com.masselis.tpmsadvanced.interfaces.screens

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.isNotEnabled
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.ClearBoundSensorsButtonTags
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.DeleteVehicleButtonTags
import com.masselis.tpmsadvanced.interfaces.composable.HomeTags
import com.masselis.tpmsadvanced.interfaces.composable.SettingsTag
import com.masselis.tpmsadvanced.interfaces.tools.check

context (ComposeTestRule)
@OptIn(ExperimentalTestApi::class)
internal class Settings {
    private val backButton
        get() = onNodeWithTag(HomeTags.backButton)

    private val deleteVehicleButton
        get() = onNodeWithTag(DeleteVehicleButtonTags.Button.tag)

    private val clearFavouritesButton
        get() = onNodeWithTag(ClearBoundSensorsButtonTags.tag)

    fun assertVehicleSettingsDisplayed() =
        onNodeWithTag(SettingsTag.vehicle).assertIsDisplayed()

    fun deleteVehicle(block: DeleteVehicleDialog.() -> Unit) {
        deleteVehicleButton.performScrollTo()
        deleteVehicleButton.performClick()
        DeleteVehicleDialog().block()
        waitUntilDoesNotExist(hasTestTag(DeleteVehicleButtonTags.Dialog.delete))
    }

    fun assertVehicleDeleteIsNotEnabled() = deleteVehicleButton.assertIsNotEnabled()

    fun clearFavourites() {
        clearFavouritesButton.performScrollTo()
        clearFavouritesButton.performClick()
    }

    fun waitClearFavouritesEnabled() = waitUntil { clearFavouritesButton.check(isEnabled()) }

    fun waitClearFavouritesDisabled() = clearFavouritesButton.check(isNotEnabled())

    fun leave() = backButton.performClick()

}