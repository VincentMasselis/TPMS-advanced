package com.masselis.tpmsadvanced.data.car.model

import android.os.Parcelable
import com.masselis.tpmsadvanced.data.record.model.Pressure
import com.masselis.tpmsadvanced.data.record.model.Temperature
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
public data class Vehicle(
    public val uuid: UUID,
    public val kind: Kind,
    public val name: String,
    public val lowPressure: Pressure,
    public val highPressure: Pressure,
    public val lowTemp: Temperature,
    public val normalTemp: Temperature,
    public val highTemp: Temperature,
    public val isBackgroundMonitor: Boolean,
) : Parcelable {
    public enum class Kind {
        /**
         * ```
         * N-N
         *  |
         * N-N
         * ```
         */
        CAR,

        /**
         * ```
         * N-N
         * ```
         */
        SINGLE_AXLE_TRAILER,

        /**
         * ```
         *  N
         *  |
         *  N
         * ```
         */
        MOTORCYCLE,

        /**
         * ```
         * N-N
         *  N
         * ```
         */
        TADPOLE_THREE_WHEELER,

        /**
         * ```
         *  N
         * N-N
         * ```
         */
        DELTA_THREE_WHEELER;
    }
}
