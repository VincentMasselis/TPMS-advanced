package com.masselis.tpmsadvanced.unit.usecase

import android.content.Context
import androidx.core.content.edit
import com.masselis.tpmsadvanced.common.ObservableStateFlow
import com.masselis.tpmsadvanced.common.appContext
import com.masselis.tpmsadvanced.unit.ioc.SingleInstance
import com.masselis.tpmsadvanced.unit.model.PressureUnit
import com.masselis.tpmsadvanced.unit.model.TemperatureUnit
import java.util.*
import javax.inject.Inject

@SingleInstance
class UnitUseCase @Inject constructor() {

    private val sharedPreferences = appContext.getSharedPreferences(
        "UNITS",
        Context.MODE_PRIVATE
    )

    val pressure = ObservableStateFlow(
        sharedPreferences
            .getString("PRESSURE", null)
            ?.let { name -> PressureUnit.values().first { it.name == name } }
            ?: when (Locale.getDefault().isO3Country) {
                "ARG", "BRA", "USA", "CHL", "MEX", "GBR", "IND", "HKG", "CAN", "AUS", "NZL", "IRL", "BHS", "PER" -> PressureUnit.PSI
                "FRA" -> PressureUnit.BAR
                else -> PressureUnit.KILO_PASCAL
            }
    ) { _, newValue ->
        sharedPreferences.edit { putString("PRESSURE", newValue.name) }
    }

    val temperature = ObservableStateFlow(
        sharedPreferences
            .getString("TEMPERATURE", null)
            ?.let { name -> TemperatureUnit.values().first { it.name == name } }
            ?: when (Locale.getDefault().isO3Country) {
                "USA" -> TemperatureUnit.FAHRENHEIT
                else -> TemperatureUnit.CELSIUS
            }
    ) { _, newValue ->
        sharedPreferences.edit { putString("TEMPERATURE", newValue.name) }
    }
}