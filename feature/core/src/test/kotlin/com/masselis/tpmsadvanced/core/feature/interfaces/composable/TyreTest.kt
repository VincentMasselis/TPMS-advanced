package com.masselis.tpmsadvanced.core.feature.interfaces.composable

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.Paparazzi
import com.masselis.tpmsadvanced.core.test.MainDispatcherRule
import org.junit.Rule
import org.junit.Test

internal class TyreTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val paparazzi = Paparazzi(
        theme = "android:Theme.Material3.DayNight.NoActionBar"
    )

    @OptIn(ExperimentalLayoutApi::class)
    @Test
    fun tyres() {
        paparazzi.snapshot {
            FlowRow {
                Wrap { NotDetectedPreview() }
                Wrap { BlueToGreenPreview() }
                Wrap { BlueToGreen2Preview() }
                Wrap { BlueToGreen3Preview() }
                Wrap { GreenToRedPreview() }
                Wrap { GreenToRed2Preview() }
                Wrap { GreenToRed3Preview() }
                Wrap { AlertingPreview() }
                Wrap { DetectionIssuePreview() }
            }
        }
    }

    @Composable
    private fun Wrap(tyre: @Composable BoxScope.() -> Unit) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 8.dp),
            content = tyre
        )
    }
}
