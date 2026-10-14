package com.masselis.tpmsadvanced.feature.unit.ioc

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.feature.unit.interfaces.UnitsViewModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoMap
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.viewmodel.ViewModelKey

@Suppress("unused")
@BindingContainer
@ContributesTo(AppScope::class)
public object Bindings {
    @Provides
    @IntoMap
    @ViewModelKey(UnitsViewModel::class)
    private fun unitsViewModel(unitPreferences: UnitPreferences): ViewModel =
        UnitsViewModel(unitPreferences)
}
