package com.masselis.tpmsadvanced.core.feature.interfaces

import android.content.Context
import androidx.core.content.edit
import com.masselis.tpmsadvanced.core.common.observableStateFlow
import com.masselis.tpmsadvanced.core.feature.ioc.SingleInstance
import com.masselis.tpmsadvanced.data.record.model.Pressure
import com.masselis.tpmsadvanced.data.record.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.record.model.Temperature
import com.masselis.tpmsadvanced.data.record.model.Temperature.CREATOR.celsius
import javax.inject.Inject

@SingleInstance
internal class AtmosphereRangePreferences @Inject constructor(
    context: Context
) {

    private val sharedPreferences = context.getSharedPreferences(
        "ATMOSPHERE_ALERTS",
        Context.MODE_PRIVATE
    )

    val highTempFlow = temperatureSharedPreference(HIGH_TEMP_KEY, 90f.celsius)
    val normalTempFlow = temperatureSharedPreference(NORMAL_TEMP_KEY, 45f.celsius)
    val lowTempFlow = temperatureSharedPreference(LOW_TEMP_KEY, 20f.celsius)

    private fun temperatureSharedPreference(key: String, defaultValue: Temperature) =
        observableStateFlow(
            Temperature(sharedPreferences.getFloat(key, defaultValue.celsius))
        ) { _, newValue ->
            sharedPreferences.edit { putFloat(key, newValue.celsius) }
        }

    val lowPressureFlow = pressureSharedPreference(LOW_PRESSURE_KEY, 1f.bar)
    val highPressureFlow = pressureSharedPreference(HIGH_PRESSURE_KEY, 3f.bar)

    @Suppress("SameParameterValue")
    private fun pressureSharedPreference(key: String, defaultValue: Pressure) = observableStateFlow(
        Pressure(sharedPreferences.getFloat(key, defaultValue.kpa))
    ) { _, newValue ->
        sharedPreferences.edit { putFloat(key, newValue.kpa) }
    }

    internal companion object {
        private const val HIGH_TEMP_KEY = "HIGH_TEMP_KEY"
        private const val NORMAL_TEMP_KEY = "NORMAL_TEMP_KEY"
        private const val LOW_TEMP_KEY = "LOW_TEMP_KEY"

        private const val LOW_PRESSURE_KEY = "LOW_PRESSURE_KEY"
        private const val HIGH_PRESSURE_KEY = "HIGH_PRESSURE_KEY"
    }
}
