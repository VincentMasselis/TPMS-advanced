package com.masselis.tpmsadvanced.data.vehicle.ioc

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.masselis.tpmsadvanced.data.vehicle.Database
import com.masselis.tpmsadvanced.data.vehicle.ioc.InternalComponent.Companion.DebugComponent
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import dagger.Subcomponent

@Subcomponent
public interface DebugComponent {
    @Subcomponent.Factory
    public interface Factory : () -> DebugComponent

    public val driver: SqlDriver
    public val database: Database
    public val locationAdapter: ColumnAdapter<Vehicle.Kind.Location, Long>

    public companion object : DebugComponent by DebugComponent()
}
