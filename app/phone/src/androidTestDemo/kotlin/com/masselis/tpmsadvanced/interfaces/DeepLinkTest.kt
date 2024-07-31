package com.masselis.tpmsadvanced.interfaces

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.masselis.tpmsadvanced.core.database.QueryOne.Companion.asOne
import com.masselis.tpmsadvanced.data.vehicle.ioc.DebugComponent.Companion.database
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.DELTA_THREE_WHEELER
import com.masselis.tpmsadvanced.interfaces.screens.Home.Companion.home
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
internal class DeepLinkTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private fun test(
        uuid: UUID = UUID.randomUUID(),
        uri: Uri? = Uri.parse("tpmsadvanced://vehicle/$uuid")
    ) = ActivityScenario.launch<RootActivity>(
        if (uri != null) Intent(ACTION_VIEW, uri)
        else Intent(getApplicationContext(), RootActivity::class.java)
    )

    @Test
    fun openRandomUuid() = test().use {
        composeTestRule.home {}
    }

    @Test
    fun openCurrentAndOpenNewVehicle() {
        val currentName = database.vehicleQueries.currentFavourite().asOne().execute().name
        test(uri = null).use {
            composeTestRule.home {
                dropdownMenu {
                    assertCurrentVehicle(currentName)
                    close()
                }
            }
        }
        val deepLinkUuid = UUID.randomUUID()
        val deepLinkName = "Three wheeler"
        database.vehicleQueries.insert(deepLinkUuid, DELTA_THREE_WHEELER, deepLinkName, false)
        test(uuid = deepLinkUuid).use {
            composeTestRule.home {
                dropdownMenu {
                    assertCurrentVehicle(deepLinkName)
                    close()
                }
            }
        }
    }
}

