package com.masselis.tpmsadvanced.core.common

public val isRunningInstrumentedTest: Boolean by lazy {
    try {
        Class.forName("androidx.test.espresso.Espresso")
        true
    } catch (_: ClassNotFoundException) {
        false
    }
}
