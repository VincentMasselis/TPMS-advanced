package com.masselis.tpmsadvanced.analyse

import kotlinx.serialization.Serializable
import org.gradle.api.GradleException
import kotlin.math.roundToInt

@Serializable
@JvmInline
public value class Fraction(public val double: Double) : java.io.Serializable {

    public constructor(float: Float) : this(float.toDouble())

    init {
        require(double in 0.0..1.0) {
            throw GradleException("Filled value for \"Fraction\" must be a floating point value (float or double) between 0 and 1. Current value: $double")
        }
    }

    public val float: Float
        get() = double.toFloat()

    public companion object {
        public val Float.fraction: Fraction
            get() = Fraction(this)

        public val Double.fraction: Fraction
            get() = Fraction(this)
    }
}

internal fun Fraction.asPercent() = float.times(100).roundToInt()