package com.masselis.tpmsadvanced.feature.background.ioc

import com.masselis.tpmsadvanced.data.vehicle.ioc.DataVehicleComponent
import com.masselis.tpmsadvanced.feature.background.interfaces.DisableMonitorBroadcastReceiver
import com.masselis.tpmsadvanced.feature.background.interfaces.MonitorService
import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.ManualBackgroundViewModel
import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.impl.AutomaticBackgroundViewModelImpl
import com.masselis.tpmsadvanced.feature.background.usecase.CheckForPermissionUseCase
import com.masselis.tpmsadvanced.feature.background.usecase.ForegroundServiceUseCase
import com.masselis.tpmsadvanced.feature.background.usecase.VehiclesToMonitorUseCase
import com.masselis.tpmsadvanced.feature.main.ioc.FeatureMainComponent
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Includes
import dev.zacsweers.metro.createGraphFactory

public interface FeatureBackgroundComponent {

    public val vehiclesToMonitorUseCase: VehiclesToMonitorUseCase

    public companion object : FeatureBackgroundComponent by InternalComponent
}

@Suppress("PropertyName", "VariableNaming", "unused")
@DependencyGraph(
    AppScope::class,
    bindingContainers = [Bindings::class]
)
internal interface InternalComponent : FeatureBackgroundComponent {

    @DependencyGraph.Factory
    interface Factory {
        fun build(
            @Includes featureMainComponent: FeatureMainComponent,
            @Includes dataVehicleComponent: DataVehicleComponent,
        ): InternalComponent
    }

    val checkForPermissionUseCase: CheckForPermissionUseCase
    val foregroundServiceUseCase: ForegroundServiceUseCase
    val AutomaticBackgroundViewModel: AutomaticBackgroundViewModelImpl.Factory
    val ManualBackgroundViewModel: ManualBackgroundViewModel.Factory

    fun inject(injectable: MonitorService)

    fun inject(disableMonitorBroadcastReceiver: DisableMonitorBroadcastReceiver)

    companion object : InternalComponent by createGraphFactory<Factory>().build(
        FeatureMainComponent,
        DataVehicleComponent,
    ) {
        init {
            // Theses use cases do stuff in background when initialized
            checkForPermissionUseCase
            foregroundServiceUseCase
        }
    }
}
