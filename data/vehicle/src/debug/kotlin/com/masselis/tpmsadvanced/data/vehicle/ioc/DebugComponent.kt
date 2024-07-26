package com.masselis.tpmsadvanced.data.vehicle.ioc

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.masselis.tpmsadvanced.data.vehicle.Database
import com.masselis.tpmsadvanced.data.vehicle.ioc.InternalComponent.Companion.DebugComponent
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle

internal interface DebugComponent {

    val driver: SqlDriver
    val database: Database
    val locationAdapter: ColumnAdapter<Vehicle.Kind.Location, Long>

    companion object : DebugComponent by DebugComponent()
}
