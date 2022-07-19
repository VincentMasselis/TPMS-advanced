package com.masselis.tpmsadvanced.core.usecase

import android.content.Context
import androidx.core.content.edit
import com.masselis.tpmsadvanced.common.appContext
import com.masselis.tpmsadvanced.core.ioc.CoreSingleton
import com.masselis.tpmsadvanced.core.model.Pressure
import com.masselis.tpmsadvanced.core.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.core.model.Temperature
import com.masselis.tpmsadvanced.core.model.Temperature.CREATOR.celsius
import com.masselis.tpmsadvanced.common.ObservableStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@CoreSingleton
class AtmosphereRangeUseCase @Inject constructor() {

    private val sharedPreferences = appContext.getSharedPreferences(
        "ATMOSPHERE_ALERTS",
        Context.MODE_PRIVATE
    )

    val highTempFlow: MutableStateFlow<Temperature> =
        temperatureSharedPreference(HIGH_TEMP_KEY, 90f.celsius)
    val normalTempFlow: MutableStateFlow<Temperature> =
        temperatureSharedPreference(NORMAL_TEMP_KEY, 45f.celsius)
    val lowTempFlow: MutableStateFlow<Temperature> =
        temperatureSharedPreference(LOW_TEMP_KEY, 20f.celsius)

    val lowPressureFlow: MutableStateFlow<Pressure> =
        pressureSharedPreference(LOW_PRESSURE_KEY, 1f.bar)

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