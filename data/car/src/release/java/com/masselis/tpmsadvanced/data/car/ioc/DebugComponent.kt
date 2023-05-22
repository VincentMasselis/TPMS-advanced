package com.masselis.tpmsadvanced.data.car.ioc

import dagger.Subcomponent

@Subcomponent
public interface DebugComponent {
    @Subcomponent.Factory
    public interface Factory {
        public fun build(): DebugComponent
    }
}
