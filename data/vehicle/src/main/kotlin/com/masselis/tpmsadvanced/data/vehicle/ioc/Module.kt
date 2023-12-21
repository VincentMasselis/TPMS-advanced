package com.masselis.tpmsadvanced.data.vehicle.ioc

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
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
import com.masselis.tpmsadvanced.data.vehicle.interfaces.BluetoothLeScanner
import com.masselis.tpmsadvanced.data.vehicle.interfaces.impl.BluetoothLeScannerImpl
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import dagger.Provides
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import java.util.UUID
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle as VehicleModel

@dagger.Module
internal object Module {

    @Provides
    fun scanner(impl: BluetoothLeScannerImpl): BluetoothLeScanner = impl

    @Provides
    fun uuidAdapter() = object : ColumnAdapter<UUID, String> {
        override fun decode(databaseValue: String): UUID = UUID.fromString(databaseValue)
        override fun encode(value: UUID): String = value.toString()
    }

    @Provides
    fun tyreLocationAdapter() = object : ColumnAdapter<SensorLocation, Long> {
        override fun decode(databaseValue: Long): SensorLocation = SensorLocation.entries
            .first { it.ordinal.toLong() == databaseValue }

        override fun encode(value: SensorLocation): Long = value.ordinal.toLong()
    }

    @Provides
    fun pressureAdapter() = object : ColumnAdapter<Pressure, Double> {
        override fun decode(databaseValue: Double): Pressure = Pressure(databaseValue.toFloat())
        override fun encode(value: Pressure): Double = value.kpa.toDouble()
    }

    @Provides
    fun temperatureAdapter() = object : ColumnAdapter<Temperature, Double> {
        override fun decode(databaseValue: Double): Temperature =
            Temperature(databaseValue.toFloat())

        override fun encode(value: Temperature): Double = value.celsius.toDouble()
    }

    @Provides
    fun uShortAdapter() = object : ColumnAdapter<UShort, Long> {
        override fun decode(databaseValue: Long): UShort = databaseValue.toUShort()
        override fun encode(value: UShort): Long = value.toLong()
    }

    @Suppress("MagicNumber")
    @Provides
    fun locationAdapter() = object : ColumnAdapter<VehicleModel.Kind.Location, Long> {

        override fun encode(value: VehicleModel.Kind.Location): Long = when (value) {
            is VehicleModel.Kind.Location.Axle -> 0L + value.axle.ordinal.toLong()
            is VehicleModel.Kind.Location.Side -> 10L + value.side.ordinal
            is VehicleModel.Kind.Location.Wheel -> 20L + value.location.ordinal
        }

        override fun decode(databaseValue: Long): VehicleModel.Kind.Location {
            val ordinal = databaseValue.toInt() % 10
            return when (databaseValue) {
                in 0..9 -> VehicleModel.Kind.Location.Axle(
                    SensorLocation.Axle.entries.first { it.ordinal == ordinal }
                )

                in 10..19 -> VehicleModel.Kind.Location.Side(
                    SensorLocation.Side.entries.first { it.ordinal == ordinal }
                )

                in 20..29 -> VehicleModel.Kind.Location.Wheel(
                    SensorLocation.entries.first { it.ordinal == ordinal }
                )

                else -> error("Unable to parse this input $databaseValue")
            }
        }
    }

    @DataVehicleComponent.Scope
    @Provides
    fun driver(): SqlDriver = AndroidSqliteDriver(
        schema = Database.Schema,
        context = appContext,
        name = "car.db",
        factory = RequerySQLiteOpenHelperFactory(),
        callback = object : SupportSQLiteOpenHelper.Callback(Database.Schema.version.toInt()) {

            val delegate = AndroidSqliteDriver.Callback(Database.Schema)

            override fun onConfigure(db: SupportSQLiteDatabase) {
                delegate.onConfigure(db)
                db.setForeignKeyConstraintsEnabled(true)
                // Increase SQLite performance, see https://developer.android.com/topic/performance/sqlite-performance-best-practices
                db.enableWriteAheadLogging()
                db.execSQL("PRAGMA synchronous = NORMAL")
            }

            override fun onCreate(db: SupportSQLiteDatabase) {
                delegate.onCreate(db)
            }

            override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                delegate.onUpgrade(db, oldVersion, newVersion)
            }
        }
    )

    @DataVehicleComponent.Scope
    @Provides
    fun database(
        driver: SqlDriver,
        uuidAdapter: ColumnAdapter<UUID, String>,
        sensorLocationAdapter: ColumnAdapter<VehicleModel.Kind.Location, Long>,
        pressureAdapter: ColumnAdapter<Pressure, Double>,
        temperatureAdapter: ColumnAdapter<Temperature, Double>,
        uShortAdapter: ColumnAdapter<UShort, Long>,
    ) = Database(
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
