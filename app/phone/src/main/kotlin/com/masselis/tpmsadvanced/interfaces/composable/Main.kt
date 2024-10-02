package com.masselis.tpmsadvanced.interfaces.composable

import androidx.compose.runtime.Composable
import com.masselis.tpmsadvanced.feature.main.interfaces.composable.Preconditions
import java.util.UUID

@Composable
internal fun Main(expectedVehicle: UUID?) {
    Preconditions {
        Home(expectedVehicle)
    }
}
