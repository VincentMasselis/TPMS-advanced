@file:Suppress("MatchingDeclarationName")
package com.masselis.tpmsadvanced.interfaces

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
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
        DropdownMenu().block()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(dropdownEntryAddVehicle).assertDoesNotExist()
    }

    inner class DropdownMenu {
        private val addVehicle get() = composeTestRule.onNodeWithTag(dropdownEntryAddVehicle)

        init {
            composeTestRule.waitUntilExactlyOneExists(hasTestTag(dropdownEntryAddVehicle))
        }

        fun addVehicle(block: AddVehicle.() -> Unit) {
            addVehicle.performClick()
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

            init {
                composeTestRule.waitUntilExactlyOneExists(hasTestTag(CurrentVehicleDropdownTags.AddVehicle.addButton))
            }

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
        composeTestRule.waitForIdle()
        return composeTestRule.onNodeWithTag(BindSensorTags.Button.tag(manySensor))
    }

    fun isBindSensorAvailable(manySensor: ManySensor): Boolean = bindSensorButton(manySensor)
        .isDisplayed()

    fun bindSensorDialog(manySensor: ManySensor, block: BindSensorDialog.() -> Unit) {
        bindSensorButton(manySensor).performClick()
        BindSensorDialog().block()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag(BindSensorTags.Dialog.addToFavoritesTag)
            .assertDoesNotExist()
    }

    @OptIn(ExperimentalTestApi::class)
    inner class BindSensorDialog {
        private val addToFavoritesTag
            get() = composeTestRule.onNodeWithTag(BindSensorTags.Dialog.addToFavoritesTag)
        private val cancelTag
            get() = composeTestRule.onNodeWithTag(BindSensorTags.Dialog.cancelTag)

        init {
            composeTestRule.waitUntilExactlyOneExists(hasTestTag(BindSensorTags.Dialog.addToFavoritesTag))
        }

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
            deleteVehicleButton.performScrollTo()
            deleteVehicleButton.performClick()
            DeleteVehicleDialog().block()
            composeTestRule.waitUntilDoesNotExist(hasTestTag(DeleteVehicleButtonTags.Dialog.delete))
        }

        fun isVehicleDeleteEnabled() =
            deleteVehicleButton.check(isEnabled())

        fun clearFavourites() {
            clearFavouritesButton.performScrollTo()
            clearFavouritesButton.performClick()
        }

        fun isClearFavouritesEnabled() = clearFavouritesButton.check(isEnabled())

        fun leave() = Espresso.pressBack()

        inner class DeleteVehicleDialog {
            private val deleteButton
                get() = composeTestRule.onNodeWithTag(DeleteVehicleButtonTags.Dialog.delete)
            private val cancelButton
                get() = composeTestRule.onNodeWithTag(DeleteVehicleButtonTags.Dialog.cancel)

            init {
                composeTestRule.waitUntilExactlyOneExists(hasTestTag(DeleteVehicleButtonTags.Dialog.delete))
            }

            fun delete() = deleteButton.performClick()

            fun cancel() = cancelButton.performClick()
        }
    }

    companion object {
        @Suppress("MemberNameEqualsClassName")
        internal fun ComposeTestRule.home(block: Home.() -> Unit) {
            waitForIdle()
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
