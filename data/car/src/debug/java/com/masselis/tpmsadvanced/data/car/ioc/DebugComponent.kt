package com.masselis.tpmsadvanced.data.car.ioc

import app.cash.sqldelight.db.SqlDriver
import com.masselis.tpmsadvanced.data.car.Database
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