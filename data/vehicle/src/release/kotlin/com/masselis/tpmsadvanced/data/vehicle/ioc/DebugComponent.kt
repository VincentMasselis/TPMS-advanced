package com.masselis.tpmsadvanced.data.vehicle.ioc

import dagger.Subcomponent

@Subcomponent
public interface DebugComponent {
    @Subcomponent.Factory
    public interface Factory : () -> DebugComponent
}
