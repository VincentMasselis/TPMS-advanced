package com.masselis.tpmsadvanced.usecase

import android.content.Context
import androidx.core.content.edit
import com.masselis.tpmsadvanced.model.Pressure
import com.masselis.tpmsadvanced.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.model.Temperature
import com.masselis.tpmsadvanced.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.tools.ObservableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AtmosphereRangeUseCase @Inject constructor(
    context: Context
) {

    private val sharedPreferences = context.getSharedPreferences(
        "ATMOSPHERE_ALERTS",
        Context.MODE_PRIVATE
    )

    val highTempFlow = temperatureSharedPreference(HIGH_TEMP_KEY, 90f.celsius)
    val normalTempFlow = temperatureSharedPreference(NORMAL_TEMP_KEY, 45f.celsius)
    val lowTempFlow = temperatureSharedPreference(LOW_TEMP_KEY, 20f.celsius)

    val lowPressureFlow = pressureSharedPreference(LOW_PRESSURE_KEY, 1f.bar)

    private fun temperatureSharedPreference(key: String, defaultValue: Temperature) =
        ObservableStateFlow(
            Temperature(sharedPreferences.getFloat(key, defaultValue.celsius))
        ) { _, newValue ->
            sharedPreferences.edit { putFloat(key, newValue.celsius) }
        }

    @Suppress("SameParameterValue")
    private fun pressureSharedPreference(key: String, defaultValue: Pressure) = ObservableStateFlow(
        Pressure(sharedPreferences.getFloat(key, defaultValue.kpa))
    ) { _, newValue ->
        sharedPreferences.edit { putFloat(key, newValue.kpa) }
    }

    companion object {
        private const val HIGH_TEMP_KEY = "HIGH_TEMP_KEY"
        private const val NORMAL_TEMP_KEY = "NORMAL_TEMP_KEY"
        private const val LOW_TEMP_KEY = "LOW_TEMP_KEY"
        private const val LOW_PRESSURE_KEY = "LOW_PRESSURE_KEY"
    }
}