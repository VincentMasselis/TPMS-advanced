package com.masselis.tpmsadvanced.unit.model

enum class PressureUnit {
    KILO_PASCAL,
    BAR,
    PSI;

    fun string() = when (this) {
        KILO_PASCAL -> "kpa"
        BAR -> "bar"
        PSI -> "psi"
    }
}