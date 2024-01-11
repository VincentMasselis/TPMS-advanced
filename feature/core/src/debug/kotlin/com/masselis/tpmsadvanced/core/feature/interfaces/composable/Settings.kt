package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.isNotEnabled
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.masselis.tpmsadvanced.core.androidtest.ExitToken
import com.masselis.tpmsadvanced.core.androidtest.Screen
import com.masselis.tpmsadvanced.core.androidtest.check

context (ComposeTestRule)
@OptIn(ExperimentalTestApi::class)
public class Settings(
    private val backButtonTag: String,
    private val containerTag: String,
    block: Settings.() -> ExitToken<Settings>
) : Screen<Settings>(block) {
    private val backButton
        get() = onNodeWithTag(backButtonTag)

    private val deleteVehicleButton
        get() = onNodeWithTag(DeleteVehicleButtonTags.Button.tag)

    private val clearFavouritesButton
        get() = onNodeWithTag(ClearBoundSensorsButtonTags.tag)

    init {
        runBlock()
    }

    public fun assertVehicleSettingsDisplayed() {
        onNodeWithTag(containerTag).assertIsDisplayed()
    }

    public fun deleteVehicle(block: DeleteVehicleDialog.() -> ExitToken<DeleteVehicleDialog>): ExitToken<Settings> {
        deleteVehicleButton.performScrollTo()
        deleteVehicleButton.performClick()
        DeleteVehicleDialog(block)
        waitUntilDoesNotExist(hasTestTag(DeleteVehicleButtonTags.Dialog.delete))
        return exitToken
    }

    public fun assertVehicleDeleteIsNotEnabled() {
        deleteVehicleButton.assertIsNotEnabled()
    }

    public fun clearFavourites() {
        clearFavouritesButton.performScrollTo()
        clearFavouritesButton.performClick()
    }

    public fun waitClearFavouritesEnabled(): Unit =
        waitUntil { clearFavouritesButton.check(isEnabled()) }

    public fun waitClearFavouritesDisabled() {
        clearFavouritesButton.check(isNotEnabled())
    }

    public fun leave(): ExitToken<Settings> {
        backButton.performClick()
        return exitToken
    }
}