package com.masselis.tpmsadvanced.unit.model

enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT;

    fun string() = when (this) {
        CELSIUS -> "celsius"
        FAHRENHEIT -> "fahrenheit"
    }
}