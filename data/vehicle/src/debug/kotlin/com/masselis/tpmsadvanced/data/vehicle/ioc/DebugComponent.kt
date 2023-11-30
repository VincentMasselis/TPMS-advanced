package com.masselis.tpmsadvanced.data.vehicle.ioc

import app.cash.sqldelight.db.SqlDriver
import com.masselis.tpmsadvanced.data.vehicle.Database
import dagger.Subcomponent

@Subcomponent
internal interface DebugComponent {
    @Subcomponent.Factory
    interface Factory {
        fun build(): DebugComponent
    }

    val driver: SqlDriver
    val database: Database
}
