package com.masselis.tpmsadvanced.feature.main.interfaces.composable

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
import com.masselis.tpmsadvanced.core.androidtest.EnterExitComposable
import com.masselis.tpmsadvanced.core.androidtest.EnterExitComposable.ExitToken
import com.masselis.tpmsadvanced.core.androidtest.EnterExitComposable.Instructions
import com.masselis.tpmsadvanced.core.androidtest.check
import com.masselis.tpmsadvanced.core.androidtest.onEnterAndOnExit
import com.masselis.tpmsadvanced.core.androidtest.process
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.DeleteVehicleDialog.Companion.invoke as DeleteVehicleDialog

@OptIn(ExperimentalTestApi::class)
public class Settings(
    private val backButtonTag: String,
    private val containerTag: String,
    composeTestRule: ComposeTestRule,
) :
    ComposeTestRule by composeTestRule,
    EnterExitComposable<Settings> by onEnterAndOnExit(
        { composeTestRule.waitUntilExactlyOneExists(hasTestTag(containerTag)) },
        { composeTestRule.waitUntilDoesNotExist(hasTestTag(containerTag)) }
    ) {
    private val backButton
        get() = onNodeWithTag(backButtonTag)

    private val deleteVehicleButton
        get() = onNodeWithTag(DeleteVehicleButtonTags.Button.tag)

    private val clearFavouritesButton
        get() = onNodeWithTag(ClearBoundSensorsButtonTags.root)

    private val deleteVehicleDialogTest = DeleteVehicleDialog()

    public fun assertVehicleSettingsDisplayed() {
        onNodeWithTag(containerTag).assertIsDisplayed()
    }

    public fun deleteVehicle(instructions: Instructions<DeleteVehicleDialog>): ExitToken<Settings> {
        deleteVehicleButton.performScrollTo()
        deleteVehicleButton.performClick()
        deleteVehicleDialogTest.process(instructions)
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
        waitUntil { clearFavouritesButton.check(isNotEnabled()) }
    }

    public fun leave(): ExitToken<Settings> {
        backButton.performClick()
        return exitToken
    }

    public companion object {
        public operator fun ComposeTestRule.invoke(
            backButtonTag: String,
            containerTag: String
        ): Settings = Settings(backButtonTag, containerTag, this)
    }
}