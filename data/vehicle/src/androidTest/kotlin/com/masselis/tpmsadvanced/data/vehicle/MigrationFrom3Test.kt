package com.masselis.tpmsadvanced.data.vehicle

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.core.common.appGraph
import com.masselis.tpmsadvanced.data.vehicle.interfaces.afterVersion3
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import io.requery.android.database.sqlite.SQLiteDatabase
import io.requery.android.database.sqlite.SQLiteDatabase.OPEN_READWRITE
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class MigrationFrom3Test {

    @ContributesTo(AppScope::class)
    internal interface Extractor {
        val locationAdapter: ColumnAdapter<Vehicle.Kind.Location, Long>
    }

    private lateinit var locationAdapter: ColumnAdapter<Vehicle.Kind.Location, Long>

    @Before
    fun setup() {
        locationAdapter = (appGraph as Extractor).locationAdapter
    }

    @Test
    fun test() {
        val dbFile = appContext.getDatabasePath("car.db")
        dbFile.delete()
        appContext.assets.open("3.db").use { input ->
            dbFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        Database.Schema.migrate(
            AndroidSqliteDriver(
                SQLiteDatabase.openDatabase(
                    dbFile.absolutePath,
                    null,
                    OPEN_READWRITE
                )
            ),
            3,
            4,
            Database.afterVersion3(locationAdapter)
        )
    }
}