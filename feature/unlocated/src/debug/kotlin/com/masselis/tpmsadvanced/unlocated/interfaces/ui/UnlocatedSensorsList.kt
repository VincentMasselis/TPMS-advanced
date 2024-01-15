package com.masselis.tpmsadvanced.unlocated.interfaces.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.masselis.tpmsadvanced.core.androidtest.OneOffComposable
import com.masselis.tpmsadvanced.core.androidtest.OneOffComposable.ExitToken
import com.masselis.tpmsadvanced.core.androidtest.OneOffComposable.Instructions
import com.masselis.tpmsadvanced.core.androidtest.oneOffComposable
import com.masselis.tpmsadvanced.core.androidtest.process
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle

context(ComposeTestRule)
@OptIn(ExperimentalTestApi::class)
public class UnlocatedSensorsList : OneOffComposable<UnlocatedSensorsList> by oneOffComposable(
    { waitUntilExactlyOneExists(hasTestTag(UnlocatedSensorListTags.root)) },
    { waitUntilDoesNotExist(hasTestTag(UnlocatedSensorListTags.root)) },
) {
    private val sensorUnpluggedButtonNode
        get() = onNodeWithTag(UnlocatedSensorListTags.sensorUnpluggedButton)

    private val goBackButtonNode
        get() = onNodeWithTag(UnlocatedSensorListTags.bindingFinishedGoBackButton)

    private fun tyreCellCardNode(sensorId: Int) =
        onNodeWithTag(UnlocatedSensorListTags.tyreCell(sensorId))

    private val bindDialogTest = BindDialog()

    public fun tapSensorUnplugged() {
        sensorUnpluggedButtonNode.performClick()
    }

    public fun tapSensor(sensorId: Int, instructions: Instructions<BindDialog>) {
        tyreCellCardNode(sensorId).performClick()
        bindDialogTest.process(instructions)
    }

    public fun assertAllLocationBound(vararg locations: Pair<Int, Vehicle.Kind.Location>) {
        locations.forEach { (sensorId, location) ->
            onAllNodesWithTag(VehicleTyresTags.tyreLocation(location))
                .filterToOne(hasAnyAncestor(hasTestTag(UnlocatedSensorListTags.boundCell(sensorId))))
                .assertExists()
        }
    }

    public fun tapGoBack(): ExitToken<UnlocatedSensorsList> {
        goBackButtonNode.performClick()
        return exitToken
    }
}