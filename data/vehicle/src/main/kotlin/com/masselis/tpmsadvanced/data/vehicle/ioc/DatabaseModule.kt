package com.masselis.tpmsadvanced.data.vehicle.ioc

import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.data.vehicle.Database
import com.masselis.tpmsadvanced.data.vehicle.Sensor
import com.masselis.tpmsadvanced.data.vehicle.Tyre
import com.masselis.tpmsadvanced.data.vehicle.Vehicle
import com.masselis.tpmsadvanced.data.vehicle.interfaces.SensorDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.TyreDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.VehicleDatabase
import com.masselis.tpmsadvanced.data.vehicle.interfaces.afterVersion3
import com.masselis.tpmsadvanced.data.vehicle.ioc.Adapter.*
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue
import org.koin.dsl.module
import java.util.UUID

private enum class Adapter : Qualifier {
    UUID,
    PRESSURE,
    TEMPERATURE,
    U_SHORT,
    LOCATION;

    override val value: QualifierValue = "vehicle_dtb_adapter_${toString().lowercase()}"
}

internal val DatabaseModule = module {

    factory { SensorDatabase(get()) }
    factory { TyreDatabase(get()) }
    single(createdAtStart = true) { VehicleDatabase(get()) }

    factory(UUID) {
        object : ColumnAdapter<UUID, String> {
            override fun decode(databaseValue: String): UUID = UUID.fromString(databaseValue)
            override fun encode(value: UUID): String = value.toString()
        }
    }
    factory(PRESSURE) {
        object : ColumnAdapter<Pressure, Double> {
            override fun decode(databaseValue: Double): Pressure = Pressure(databaseValue.toFloat())
            override fun encode(value: Pressure): Double = value.kpa.toDouble()
        }
    }
    factory(TEMPERATURE) {
        object : ColumnAdapter<Temperature, Double> {
            override fun decode(databaseValue: Double): Temperature =
                Temperature(databaseValue.toFloat())

            override fun encode(value: Temperature): Double = value.celsius.toDouble()
        }
    }
    factory(U_SHORT) {
        object : ColumnAdapter<UShort, Long> {
            override fun decode(databaseValue: Long): UShort = databaseValue.toUShort()
            override fun encode(value: UShort): Long = value.toLong()
        }
    }
    factory(LOCATION) {
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
    }
    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = Database.Schema,
            context = appContext,
            name = "car.db",
            factory = RequerySQLiteOpenHelperFactory(),
            callback = object : AndroidSqliteDriver.Callback(
                Database.Schema,
                Database.afterVersion3(get(LOCATION)),
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
    }
    single<Database> {
        Database(
            get(),
            VehicleAdapter = Vehicle.Adapter(
                get(UUID),
                get(PRESSURE),
                get(PRESSURE),
                get(TEMPERATURE),
                get(TEMPERATURE),
                get(TEMPERATURE),
                EnumColumnAdapter(),
            ),
            SensorAdapter = Sensor.Adapter(
                IntColumnAdapter,
                get(LOCATION),
                get(UUID)
            ),
            TyreAdapter = Tyre.Adapter(
                IntColumnAdapter,
                IntColumnAdapter,
                get(LOCATION),
                get(PRESSURE),
                get(TEMPERATURE),
                get(U_SHORT),
                get(UUID),
            )
        )
    }
}
