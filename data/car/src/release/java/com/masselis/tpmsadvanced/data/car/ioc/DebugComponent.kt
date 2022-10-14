package com.masselis.tpmsadvanced.data.car.ioc

import dagger.Subcomponent

@Subcomponent
internal interface DebugComponent {
    @Subcomponent.Factory
    interface Factory {
        fun build(): DebugComponent
    }
}