package com.masselis.tpmsadvanced.data.vehicle.ioc

import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.core.database.SQLiteOpenHelperUseCase
import com.masselis.tpmsadvanced.data.vehicle.Database
import com.masselis.tpmsadvanced.data.vehicle.Sensor
import com.masselis.tpmsadvanced.data.vehicle.Tyre
import com.masselis.tpmsadvanced.data.vehicle.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.TyreDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.afterVersion3
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import java.util.UUID

@Suppress("unused")
@BindingContainer
internal object Bindings {

    @Provides
    @SingleIn(AppScope::class)
    private fun vehicleDatabase(database: Database): VehicleDatabase = VehicleDatabase(database)

    @Provides
    private fun sensorDatabase(database: Database): SensorDatabase = SensorDatabase(database)

    @Provides
    private fun tyreDatabase(database: Database): TyreDatabase = TyreDatabase(database)

    @Provides
    private fun uuidAdapter(): ColumnAdapter<UUID, String> = object : ColumnAdapter<UUID, String> {
        override fun decode(databaseValue: String): UUID = UUID.fromString(databaseValue)
        override fun encode(value: UUID): String = value.toString()
    }

    @Provides
    private fun tyreLocationAdapter(): ColumnAdapter<SensorLocation, Long> =
        object : ColumnAdapter<SensorLocation, Long> {
            override fun decode(databaseValue: Long): SensorLocation = SensorLocation.entries
                .first { it.ordinal.toLong() == databaseValue }

            override fun encode(value: SensorLocation): Long = value.ordinal.toLong()
        }

    @Provides
    private fun pressureAdapter(): ColumnAdapter<Pressure, Double> =
        object : ColumnAdapter<Pressure, Double> {
            override fun decode(databaseValue: Double): Pressure = Pressure(databaseValue.toFloat())
            override fun encode(value: Pressure): Double = value.kpa.toDouble()
        }

    @Provides
    private fun temperatureAdapter(): ColumnAdapter<Temperature, Double> =
        object : ColumnAdapter<Temperature, Double> {
            override fun decode(databaseValue: Double): Temperature =
                Temperature(databaseValue.toFloat())

            override fun encode(value: Temperature): Double = value.celsius.toDouble()
        }

    @Provides
    private fun uShortAdapter(): ColumnAdapter<UShort, Long> =
        object : ColumnAdapter<UShort, Long> {
            override fun decode(databaseValue: Long): UShort = databaseValue.toUShort()
            override fun encode(value: UShort): Long = value.toLong()
        }

    @Suppress("MagicNumber")
    @Provides
    private fun locationAdapter(): ColumnAdapter<Location, Long> =
        object : ColumnAdapter<Location, Long> {

            override fun encode(value: Location): Long = when (value) {
                is Location.Axle -> 0L + value.axle.ordinal.toLong()
                is Location.Side -> 10L + value.side.ordinal
                is Location.Wheel -> 20L + value.location.ordinal
            }

            override fun decode(databaseValue: Long): Location {
                val ordinal = databaseValue.toInt() % 10
                return when (databaseValue) {
                    in 0..9 -> Location.Axle(
                        SensorLocation.Axle.entries.first { it.ordinal == ordinal }
                    )

                    in 10..19 -> Location.Side(
                        SensorLocation.Side.entries.first { it.ordinal == ordinal }
                    )

                    in 20..29 -> Location.Wheel(
                        SensorLocation.entries.first { it.ordinal == ordinal }
                    )

                    else -> error("Unable to parse this input $databaseValue")
                }
            }
        }

    @Provides
    @SingleIn(AppScope::class)
    private fun driver(
        useCase: SQLiteOpenHelperUseCase,
        locationAdapter: ColumnAdapter<Location, Long>
    ): SqlDriver = AndroidSqliteDriver(
        schema = Database.Schema,
        context = appContext,
        name = "car.db",
        factory = useCase.factory,
        callback = object : AndroidSqliteDriver.Callback(
            Database.Schema,
            Database.afterVersion3(locationAdapter),
        ) {
            val delegate = AndroidSqliteDriver.Callback(Database.Schema)

            override fun onConfigure(db: SupportSQLiteDatabase) {
                delegate.onConfigure(db)
                db.setForeignKeyConstraintsEnabled(true)
                // Increase SQLite performance, see https://developer.android.com/topic/performance/sqlite-performance-best-practices
                db.enableWriteAheadLogging()
                db.execSQL("PRAGMA synchronous = NORMAL")
            }
        },
    )

    @Provides
    @SingleIn(AppScope::class)
    private fun database(
        driver: SqlDriver,
        uuidAdapter: ColumnAdapter<UUID, String>,
        sensorLocationAdapter: ColumnAdapter<Location, Long>,
        pressureAdapter: ColumnAdapter<Pressure, Double>,
        temperatureAdapter: ColumnAdapter<Temperature, Double>,
        uShortAdapter: ColumnAdapter<UShort, Long>,
    ): Database = Database(
        driver,
        VehicleAdapter = Vehicle.Adapter(
            uuidAdapter,
            pressureAdapter,
            pressureAdapter,
            temperatureAdapter,
            temperatureAdapter,
            temperatureAdapter,
            EnumColumnAdapter(),
        ),
        SensorAdapter = Sensor.Adapter(IntColumnAdapter, sensorLocationAdapter, uuidAdapter),
        TyreAdapter = Tyre.Adapter(
            IntColumnAdapter,
            IntColumnAdapter,
            sensorLocationAdapter,
            pressureAdapter,
            temperatureAdapter,
            uShortAdapter,
            uuidAdapter,
        )
    )
}
