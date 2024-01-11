package com.masselis.tpmsadvanced.unlocated.interfaces.ui

import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.core.androidtest.ExitToken
import com.masselis.tpmsadvanced.core.androidtest.Screen
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle

context(ComposeTestRule)
public class UnlocatedSensorsList(block: UnlocatedSensorsList.() -> ExitToken<UnlocatedSensorsList>) :
    Screen<UnlocatedSensorsList>(block) {
    private val sensorUnpluggedButton
        get() = onNodeWithTag(UnlocatedSensorListTags.sensorUnpluggedButton)

    private val goBackButton
        get() = onNodeWithTag(UnlocatedSensorListTags.bindingFinishedGoBackButton)

    private fun tyreCellCard(sensorId: Int) =
        onNodeWithTag(UnlocatedSensorListTags.tyreCell(sensorId))

    init {
        runBlock()
    }

    public fun tapSensorUnplugged() {
        sensorUnpluggedButton.performClick()
    }

    public fun tapSensor(sensorId: Int, block: BindDialog.() -> ExitToken<BindDialog>) {
        tyreCellCard(sensorId).performClick()
        BindDialog(block)
        waitForIdle()
        onNodeWithTag(BindDialogTags.bindDialog).assertDoesNotExist()
    }

    public fun assertAllLocationBound(vararg locations: Pair<Int, Vehicle.Kind.Location>) {
        locations.forEach { (sensorId, location) ->
            onAllNodesWithTag(VehicleTyresTags.tyreLocation(location))
                .filterToOne(hasAnyAncestor(hasTestTag(UnlocatedSensorListTags.boundCell(sensorId))))
                .assertExists()
        }
    }

    public fun tapGoBack(): ExitToken<UnlocatedSensorsList> {
        goBackButton.performClick()
        return exitToken
    }
}