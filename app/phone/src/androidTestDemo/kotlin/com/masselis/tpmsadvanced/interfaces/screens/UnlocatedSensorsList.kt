package com.masselis.tpmsadvanced.interfaces.screens

import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.unlocated.interfaces.ui.BindDialogTags
import com.masselis.tpmsadvanced.unlocated.interfaces.ui.UnlocatedSensorListTags
import com.masselis.tpmsadvanced.unlocated.interfaces.ui.VehicleTyresTags

context(ComposeTestRule)
internal class UnlocatedSensorsList {
    private val sensorUnpluggedButton
        get() = onNodeWithTag(UnlocatedSensorListTags.sensorUnplugged)

    private val goBackButton
        get() = onNodeWithTag(UnlocatedSensorListTags.goBackButton)

    private fun tyreCellCard(sensorId: Int) =
        onNodeWithTag(UnlocatedSensorListTags.tyreCell(sensorId))

    fun tapSensorUnplugged() {
        sensorUnpluggedButton.performClick()
    }

    fun tapSensor(sensorId: Int, block: BindDialog.() -> Unit) {
        tyreCellCard(sensorId).performClick()
        BindDialog().block()
        waitForIdle()
        onNodeWithTag(BindDialogTags.bindDialog).assertDoesNotExist()
    }

    fun assertAllLocationBound(vararg locations: Pair<Int, Vehicle.Kind.Location>) {
        locations.forEach { (sensorId, location) ->
            onAllNodesWithTag(VehicleTyresTags.tyreLocation(location))
                .filterToOne(hasAnyAncestor(hasTestTag(UnlocatedSensorListTags.boundCell(sensorId))))
                .assertExists()
        }
    }

    fun tapGoBack() {
        goBackButton.performClick()
    }
}