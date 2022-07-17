package com.masselis.tpmsadvanced.usecase

import android.content.Context
import androidx.core.content.edit
import com.masselis.tpmsadvanced.common.appContext
import com.masselis.tpmsadvanced.model.Pressure
import com.masselis.tpmsadvanced.model.Temperature
import com.masselis.tpmsadvanced.tools.ObservableStateFlow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnitUseCase @Inject constructor() {

    private val sharedPreferences = appContext.getSharedPreferences(
        "UNITS",
        Context.MODE_PRIVATE
    )

    val pressure = ObservableStateFlow(
        sharedPreferences
            .getString("PRESSURE", null)
            ?.let { name -> Pressure.Unit.values().first { it.name == name } }
            ?: when (Locale.getDefault().isO3Country) {
                "ARG", "BRA", "USA", "CHL", "MEX", "GBR", "IND", "HKG", "CAN", "AUS", "NZL", "IRL", "BHS", "PER" -> Pressure.Unit.PSI
                "FRA" -> Pressure.Unit.BAR
                else -> Pressure.Unit.KILO_PASCAL
            }
    ) { _, newValue ->
        sharedPreferences.edit { putString("PRESSURE", newValue.name) }
    }

    val temperature = ObservableStateFlow(
        sharedPreferences
            .getString("TEMPERATURE", null)
            ?.let { name -> Temperature.Unit.values().first { it.name == name } }
            ?: when (Locale.getDefault().isO3Country) {
                "USA" -> Temperature.Unit.FAHRENHEIT
                else -> Temperature.Unit.CELSIUS
            }
    ) { _, newValue ->
        sharedPreferences.edit { putString("TEMPERATURE", newValue.name) }
    }
}