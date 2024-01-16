package com.masselis.tpmsadvanced.data.vehicle.ioc

import dagger.Subcomponent

@Subcomponent
internal interface DebugComponent {
    @Subcomponent.Factory
    interface Factory : () -> DebugComponent
}
