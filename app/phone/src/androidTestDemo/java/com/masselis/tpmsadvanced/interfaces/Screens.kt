package com.masselis.tpmsadvanced.interfaces

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.test.espresso.Espresso
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.BindSensorTags
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.ClearBoundSensorsButtonTags
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags.dropdownEntry
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.CurrentVehicleDropdownTags.dropdownEntryAddVehicle
import com.masselis.tpmsadvanced.core.feature.interfaces.composable.DeleteVehicleButtonTags
import com.masselis.tpmsadvanced.core.feature.model.ManySensor
import com.masselis.tpmsadvanced.interfaces.composable.HomeTags
import com.masselis.tpmsadvanced.data.car.model.Vehicle as VehicleModel


@OptIn(ExperimentalTestApi::class)
internal class Home private constructor(
    private val composeTestRule: ComposeTestRule
) {

    private val dropdownMenu get() = composeTestRule.onNodeWithTag(CurrentVehicleDropdownTags.dropdownMenu)
    private val settingsButton get() = composeTestRule.onNodeWithTag(HomeTags.settings)

    fun dropdownMenu(block: DropdownMenu.() -> Unit) {
        dropdownMenu.performClick()
        composeTestRule.waitForIdle()
        DropdownMenu().block()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(dropdownEntryAddVehicle).assertDoesNotExist()
    }

    inner class DropdownMenu {
        private val addVehicle get() = composeTestRule.onNodeWithTag(dropdownEntryAddVehicle)

        fun addVehicle(block: AddVehicle.() -> Unit) {
            addVehicle.performClick()
            composeTestRule.waitForIdle()
            AddVehicle().block()
            composeTestRule.waitForIdle()
            addVehicle.assertDoesNotExist()
        }

        fun exists(vehicleName: String) = composeTestRule
            .onNodeWithTag(dropdownEntry(vehicleName))
            .exists()

        fun select(vehicleName: String) = composeTestRule
            .onNodeWithTag(dropdownEntry(vehicleName))
            .performClick()

        fun close() = dropdownMenu.performClick()

        inner class AddVehicle {
            private val textField
                get() = composeTestRule.onNodeWithTag(CurrentVehicleDropdownTags.AddVehicle.textField)
            private val addButton
                get() = composeTestRule.onNodeWithTag(CurrentVehicleDropdownTags.AddVehicle.addButton)
            private val cancelButton
                get() = composeTestRule.onNodeWithTag(CurrentVehicleDropdownTags.AddVehicle.cancelButton)

            fun setVehicleName(name: String) {
                textField.performTextInput(name)
            }

            fun setKind(kind: VehicleModel.Kind) {
                composeTestRule
                    .onNodeWithTag(CurrentVehicleDropdownTags.AddVehicle.kindRadio(kind))
                    .performClick()
            }

            fun add() = addButton.performClick()
            fun cancel() = cancelButton.performClick()
        }
    }


    private fun bindSensorButton(manySensor: ManySensor): SemanticsNodeInteraction {
        try {
            composeTestRule.waitUntil(1_000) { false }
        } catch (_: ComposeTimeoutException) {

        }
        return composeTestRule.onNodeWithTag(BindSensorTags.Button.tag(manySensor))
    }

    fun isBindSensorAvailable(manySensor: ManySensor): Boolean = bindSensorButton(manySensor)
        .isDisplayed()

    fun bindSensorDialog(manySensor: ManySensor, block: BindSensorDialog.() -> Unit) {
        bindSensorButton(manySensor).performClick()
        composeTestRule.waitForIdle()
        BindSensorDialog().block()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(BindSensorTags.Dialog.addToFavoritesTag)
            .assertDoesNotExist()
    }

    inner class BindSensorDialog {
        private val addToFavoritesTag
            get() = composeTestRule.onNodeWithTag(BindSensorTags.Dialog.addToFavoritesTag)
        private val cancelTag
            get() = composeTestRule.onNodeWithTag(BindSensorTags.Dialog.cancelTag)

        fun addToFavorites() = addToFavoritesTag.performClick()
        fun cancel() = cancelTag.performClick()
    }

    fun settings(block: Settings.() -> Unit) {
        settingsButton.performClick()
        Settings().block()
        composeTestRule.waitUntilExactlyOneExists(hasTestTag(HomeTags.settings))
    }

    inner class Settings {
        private val deleteVehicleButton
            get() = composeTestRule.onNodeWithTag(DeleteVehicleButtonTags.Button.tag)

        private val clearFavouritesButton
            get() = composeTestRule.onNodeWithTag(ClearBoundSensorsButtonTags.tag)

        fun deleteVehicle(block: DeleteVehicleDialog.() -> Unit) {
            deleteVehicleButton.performClick()
            composeTestRule.waitForIdle()
            DeleteVehicleDialog().block()
            composeTestRule.waitForIdle()
            composeTestRule.waitUntilDoesNotExist(hasTestTag(DeleteVehicleButtonTags.Dialog.delete))
        }

        fun isVehicleDeleteEnabled() =
            deleteVehicleButton.check(isEnabled())

        fun clearFavourites() = clearFavouritesButton.performClick()

        fun isClearFavouritesEnabled() = clearFavouritesButton.check(isEnabled())

        fun leave() = Espresso.pressBack()

        inner class DeleteVehicleDialog {
            private val deleteButton
                get() = composeTestRule.onNodeWithTag(DeleteVehicleButtonTags.Dialog.delete)
            private val cancelButton
                get() = composeTestRule.onNodeWithTag(DeleteVehicleButtonTags.Dialog.cancel)

            fun delete() = deleteButton.performClick()

            fun cancel() = cancelButton.performClick()
        }
    }

    companion object {
        internal fun ComposeTestRule.home(block: Home.() -> Unit) {
            Home(this).block()
        }

        private fun SemanticsNodeInteraction.check(
            matcher: SemanticsMatcher,
            messagePrefixOnError: (() -> String)? = null
        ) = try {
            assert(matcher, messagePrefixOnError)
            true
        } catch (_: AssertionError) {
            false
        }

        private fun SemanticsNodeInteraction.exists() = try {
            assertExists()
            true
        } catch (_: AssertionError) {
            false
        }

        private fun SemanticsNodeInteraction.isDisplayed() = try {
            assertIsDisplayed()
            true
        } catch (_: AssertionError) {
            false
        }
    }
}
