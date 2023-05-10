package com.masselis.tpmsadvanced.data.car.ioc

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.data.car.Database
import com.masselis.tpmsadvanced.data.car.Sensor
import com.masselis.tpmsadvanced.data.car.Tyre
import com.masselis.tpmsadvanced.data.car.Vehicle
import com.masselis.tpmsadvanced.data.record.model.Pressure
import com.masselis.tpmsadvanced.data.record.model.SensorLocation
import com.masselis.tpmsadvanced.data.record.model.Temperature
import dagger.Provides
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import java.util.*

@dagger.Module
internal object Module {

    @Provides
    fun uuidAdapter() = object : ColumnAdapter<UUID, String> {
        override fun decode(databaseValue: String): UUID = UUID.fromString(databaseValue)
        override fun encode(value: UUID): String = value.toString()
    }

    @Provides
    fun tyreLocationAdapter() = object : ColumnAdapter<SensorLocation, Long> {
        override fun decode(databaseValue: Long): SensorLocation = SensorLocation
            .values()
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

    @DataVehicleComponent.Scope
    @Provides
    fun driver(): SqlDriver = AndroidSqliteDriver(
        schema = Database.Schema,
        context = appContext,
        name = "car.db",
        factory = RequerySQLiteOpenHelperFactory(),
        callback = object : SupportSQLiteOpenHelper.Callback(Database.Schema.version) {

            val delegate = AndroidSqliteDriver.Callback(Database.Schema)

            override fun onOpen(db: SupportSQLiteDatabase) {
                delegate.onOpen(db)
                db.execSQL("PRAGMA foreign_keys=ON;")
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
        sensorLocationAdapter: ColumnAdapter<SensorLocation, Long>,
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
            sensorLocationAdapter,
            pressureAdapter,
            temperatureAdapter,
            uShortAdapter,
            uuidAdapter,
        )
    )
}
