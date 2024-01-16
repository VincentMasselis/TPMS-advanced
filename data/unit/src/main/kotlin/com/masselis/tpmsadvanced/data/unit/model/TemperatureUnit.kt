package com.masselis.tpmsadvanced.data.unit.model

public enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT;

    public fun string(): String = when (this) {
        CELSIUS -> "celsius"
        FAHRENHEIT -> "fahrenheit"
    }
}
