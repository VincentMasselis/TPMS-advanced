package com.masselis.tpmsadvanced.data.vehicle.ioc

import app.cash.sqldelight.db.SqlDriver
import com.masselis.tpmsadvanced.data.vehicle.Database
import dagger.Subcomponent

@Subcomponent
public abstract class DebugComponent {
    @Subcomponent.Factory
    public interface Factory {
        public fun build(): DebugComponent
    }

    internal abstract val driver: SqlDriver
    internal abstract val database: Database
}
