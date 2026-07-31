package com.masselis.tpmsadvanced.feature.unit.ioc

import com.masselis.tpmsadvanced.core.common.appGraph
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.feature.unit.interfaces.UnitsViewModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides

@Suppress("unused", "FunctionNaming")
@ContributesTo(AppScope::class)
public interface Bindings {
    @Provides
    private fun unitsViewModel(unitPreferences: UnitPreferences): UnitsViewModel =
        UnitsViewModel(unitPreferences)

    public val featureUnitInternal: Internal

    @Inject
    public class Internal internal constructor(
        internal val unitsViewModel: () -> UnitsViewModel
    )

    public companion object : Bindings by appGraph as Bindings {
        internal fun UnitsViewModel() = featureUnitInternal.unitsViewModel()
    }
}
