package com.masselis.tpmsadvanced.feature.background.ioc

import com.masselis.tpmsadvanced.core.feature.ioc.FeatureCoreComponent
import com.masselis.tpmsadvanced.data.vehicle.ioc.DataVehicleComponent
import com.masselis.tpmsadvanced.feature.background.interfaces.DisableMonitorBroadcastReceiver
import com.masselis.tpmsadvanced.feature.background.interfaces.MonitorService
import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.impl.AutomaticBackgroundViewModelImpl
import com.masselis.tpmsadvanced.feature.background.interfaces.viewmodel.ManualBackgroundViewModel
import com.masselis.tpmsadvanced.feature.background.usecase.CheckForPermissionUseCase
import com.masselis.tpmsadvanced.feature.background.usecase.ForegroundServiceUseCase
import com.masselis.tpmsadvanced.feature.background.usecase.VehiclesToMonitorUseCase
import dagger.Component

public interface FeatureBackgroundComponent {

    @javax.inject.Scope
    public annotation class Scope

    public val vehiclesToMonitorUseCase: VehiclesToMonitorUseCase

    public companion object : FeatureBackgroundComponent by InternalComponent
}

@Suppress("PropertyName")
@FeatureBackgroundComponent.Scope
@Component(
    dependencies = [
        DataVehicleComponent::class,
        FeatureCoreComponent::class
    ]
)
internal interface InternalComponent : FeatureBackgroundComponent {
    val checkForPermissionUseCase: CheckForPermissionUseCase
    val foregroundServiceUseCase: ForegroundServiceUseCase
    @Suppress("VariableNaming")
    val AutomaticBackgroundViewModel: AutomaticBackgroundViewModelImpl.Factory
    @Suppress("VariableNaming")
    val ManualBackgroundViewModel: ManualBackgroundViewModel.Factory

    fun inject(injectable: MonitorService)

    fun inject(disableMonitorBroadcastReceiver: DisableMonitorBroadcastReceiver)

    companion object : InternalComponent by DaggerInternalComponent
        .builder()
        .featureCoreComponent(FeatureCoreComponent)
        .dataVehicleComponent(DataVehicleComponent)
        .build() {
        init {
            // Theses use cases do stuff in background when initialized
            checkForPermissionUseCase
            foregroundServiceUseCase
        }
    }
}
