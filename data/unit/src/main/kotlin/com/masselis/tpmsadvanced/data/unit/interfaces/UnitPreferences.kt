package com.masselis.tpmsadvanced.data.unit.interfaces

import android.content.Context
import androidx.core.content.edit
import com.masselis.tpmsadvanced.core.common.observableStateFlow
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit
import com.masselis.tpmsadvanced.data.unit.model.TemperatureUnit
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Locale

public class UnitPreferences internal constructor(
    context: Context
) {

    private val sharedPreferences = context.getSharedPreferences(
        "UNITS",
        Context.MODE_PRIVATE
    )

    public val pressure: MutableStateFlow<PressureUnit> = observableStateFlow(
        sharedPreferences
            .getString("PRESSURE", null)
            ?.let { name -> PressureUnit.entries.first { it.name == name } }
            ?: when (Locale.getDefault().isO3Country) {
                @Suppress("MaxLineLength")
                "USA", "GBR", "CAN", "AUS" -> PressureUnit.PSI

                else -> PressureUnit.BAR
            }
    ) { _, newValue ->
        sharedPreferences.edit { putString("PRESSURE", newValue.name) }
    }

    public val temperature: MutableStateFlow<TemperatureUnit> = observableStateFlow(
        sharedPreferences
            .getString("TEMPERATURE", null)
            ?.let { name -> TemperatureUnit.entries.first { it.name == name } }
            ?: when (Locale.getDefault().isO3Country) {
                "USA" -> TemperatureUnit.FAHRENHEIT
                else -> TemperatureUnit.CELSIUS
            }
    ) { _, newValue ->
        sharedPreferences.edit { putString("TEMPERATURE", newValue.name) }
    }
}
