package com.masselis.tpmsadvanced.data.vehicle.ioc

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.masselis.tpmsadvanced.data.vehicle.Database
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle

public interface DebugComponent {
    public val driver: SqlDriver
    public val database: Database
    public val locationAdapter: ColumnAdapter<Vehicle.Kind.Location, Long>
}
