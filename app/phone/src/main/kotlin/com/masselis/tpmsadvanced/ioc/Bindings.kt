package com.masselis.tpmsadvanced.ioc

import com.masselis.tpmsadvanced.core.common.appGraph
import com.masselis.tpmsadvanced.feature.main.usecase.NoveltyUseCase
import com.masselis.tpmsadvanced.interfaces.viewmodel.HomeViewModel
import com.masselis.tpmsadvanced.interfaces.viewmodel.VehicleHomeViewModel
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provides

@Suppress("VariableNaming", "FunctionNaming", "unused")
@ContributesTo(AppScope::class)
public interface Bindings {

    @Provides
    private fun vehicleHomeViewModel(noveltyUseCase: NoveltyUseCase): VehicleHomeViewModel =
        VehicleHomeViewModel(noveltyUseCase)

    public val appPhoneInternal: Internal

    @Inject
    public class Internal internal constructor(
        internal val homeViewModel: HomeViewModel.Factory,
        internal val vehicleHomeViewModel: () -> VehicleHomeViewModel
    )

    public companion object : Bindings by appGraph as Bindings {
        internal val HomeViewModel = appPhoneInternal.homeViewModel
        internal fun VehicleHomeViewModel() = appPhoneInternal.vehicleHomeViewModel()
    }
}
