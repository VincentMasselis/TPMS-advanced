package com.masselis.tpmsadvanced.usecase

import android.content.Context
import androidx.core.content.edit
import com.masselis.tpmsadvanced.model.Temperature
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty

@Singleton
class AtmosphereRangeUseCase @Inject constructor(
    context: Context
) {

    private val sharedPreferences = context.getSharedPreferences(
        "ATMOSPHERE_RANGE",
        Context.MODE_PRIVATE
    )

    var highTemp by sharedPreferenceRange(HIGH_TEMP_KEY, 90f)
    var normalTemp by sharedPreferenceRange(NORMAL_TEMP_KEY, 45f)
    var lowTemp by sharedPreferenceRange(LOW_TEMP_KEY, 20f)

    private fun sharedPreferenceRange(
        key: String,
        defaultValue: Float
    ): ReadWriteProperty<Any?, Temperature> = Delegates.observable(
        Temperature(sharedPreferences.getFloat(key, defaultValue))
    ) { _, _, newValue ->
        sharedPreferences.edit {
            putFloat(key, newValue.celsius)
        }
    }

    companion object {
        private const val HIGH_TEMP_KEY = "HIGH_TEMP_KEY"
        private const val NORMAL_TEMP_KEY = "NORMAL_TEMP_KEY"
        private const val LOW_TEMP_KEY = "LOW_TEMP_KEY"
    }
}