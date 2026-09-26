package com.masselis.tpmsadvanced.data.vehicle.model

import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.kpa
import kotlin.math.abs

/**
 * Corrects the pressure read from a sensor which is off by a few units: the read pressure is
 * multiplied by [multiplier] then shifted by [offset].
 */
public data class PressureCalibration(
    public val offset: Pressure,
    public val multiplier: Float,
) {

    // Compared with a margin because the values go through float unit conversions and −/+ steps
    public val hasOffset: Boolean
        get() = abs(offset.kpa) >= OFFSET_EPSILON_KPA

    public val hasMultiplier: Boolean
        get() = abs(multiplier - 1f) >= MULTIPLIER_EPSILON

    /** `false` when [applyTo] leaves every pressure as it is */
    public val adjusts: Boolean
        get() = hasOffset || hasMultiplier

    /**
     * A pressure of 0, which is how an alarming or flat tyre is reported, is left as is so an
     * [offset] never hides it. A correction below 0 is clamped to 0 for the same reason.
     */
    public fun applyTo(pressure: Pressure): Pressure = pressure
        .takeIf { it.hasPressure() }
        ?.kpa
        ?.times(multiplier)
        ?.plus(offset.kpa)
        ?.coerceAtLeast(0f)
        ?.kpa
        ?: pressure

    private companion object {
        /** Far below the finest displayed pressure, 1 kPa */
        const val OFFSET_EPSILON_KPA = 0.01f

        /** Far below the finest displayed multiplier step, 0.01 */
        const val MULTIPLIER_EPSILON = 0.0001f
    }
}
