package com.masselis.tpmsadvanced.feature.background.ioc

import com.masselis.tpmsadvanced.data.vehicle.ioc.DataVehicleComponent
import com.masselis.tpmsadvanced.feature.background.interfaces.DisableMonitorBroadcastReceiver
import com.masselis.tpmsadvanced.feature.background.interfaces.MonitorService
import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.ManualBackgroundViewModel
import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.impl.AutomaticBackgroundViewModelImpl
import com.masselis.tpmsadvanced.feature.background.usecase.CheckForPermissionUseCase
import com.masselis.tpmsadvanced.feature.background.usecase.ForegroundServiceUseCase
import com.masselis.tpmsadvanced.feature.background.usecase.VehiclesToMonitorUseCase
import com.masselis.tpmsadvanced.feature.main.ioc.FeatureMainGraph
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.createGraphFactory

public interface FeatureBackgroundComponent {

    @javax.inject.Scope
    public annotation class Scope

    public val vehiclesToMonitorUseCase: VehiclesToMonitorUseCase

    public companion object : InternalComponent by InternalComponent.Factory.build(
        DataVehicleComponent,
        FeatureMainGraph
    )   {
        init {
            // Theses use cases do stuff in background when initialized
            checkForPermissionUseCase
            foregroundServiceUseCase
        }
    }
}

@Suppress("PropertyName")
@FeatureBackgroundComponent.Scope
@DependencyGraph
internal interface InternalComponent : FeatureBackgroundComponent {

    @DependencyGraph.Factory
    interface Factory {
        fun build(
            dataVehicleComponent: DataVehicleComponent,
            featureMainGraph: FeatureMainGraph
        ): InternalComponent

        companion object : Factory by createGraphFactory()
    }

    val checkForPermissionUseCase: CheckForPermissionUseCase
    val foregroundServiceUseCase: ForegroundServiceUseCase

    @Suppress("VariableNaming")
    val AutomaticBackgroundViewModel: AutomaticBackgroundViewModelImpl.Factory

    @Suppress("VariableNaming")
    val ManualBackgroundViewModel: ManualBackgroundViewModel.Factory

    fun inject(instance: MonitorService)

    fun inject(instance: DisableMonitorBroadcastReceiver)
}
