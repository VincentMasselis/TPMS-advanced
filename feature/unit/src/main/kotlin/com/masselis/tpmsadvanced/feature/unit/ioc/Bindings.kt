package com.masselis.tpmsadvanced.feature.unit.ioc

import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.feature.unit.interfaces.UnitsViewModel
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides

@Suppress("unused")
@BindingContainer
internal object Bindings {
    @Provides
    private fun unitsViewModel(unitPreferences: UnitPreferences): UnitsViewModel =
        UnitsViewModel(unitPreferences)
}
