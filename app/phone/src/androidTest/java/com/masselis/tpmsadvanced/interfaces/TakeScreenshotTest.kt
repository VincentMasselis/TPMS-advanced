package com.masselis.tpmsadvanced.interfaces

import androidx.compose.ui.test.IdlingResource
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.masselis.tpmsadvanced.data.record.interfaces.BluetoothLeScanner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
internal class TakeScreenshotTest {

    @get:Rule
    val permissions: GrantPermissionRule = GrantPermissionRule.grant(
        *BluetoothLeScanner.missingPermission().toTypedArray(),
    )

    @get:Rule
    val composeTestRule = createAndroidComposeRule(RootActivity::class.java)

    @Test
    fun takeScreenshots() = runTest {
        composeTestRule.onNodeWithTag("settings").performClick()
    }
}