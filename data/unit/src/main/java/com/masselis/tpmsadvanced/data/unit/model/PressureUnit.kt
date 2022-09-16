package com.masselis.tpmsadvanced.data.unit.model

public enum class PressureUnit {
    KILO_PASCAL,
    BAR,
    PSI;

    public fun string(): String = when (this) {
        KILO_PASCAL -> "kpa"
        BAR -> "bar"
        PSI -> "psi"
    }
}
