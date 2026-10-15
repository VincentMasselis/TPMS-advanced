package com.masselis.tpmsadvanced.data.vehicle.model

import com.masselis.tpmsadvanced.data.unit.model.PressureUnit.BAR
import com.masselis.tpmsadvanced.data.unit.model.PressureUnit.PSI
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.bar
import com.masselis.tpmsadvanced.data.vehicle.model.Pressure.CREATOR.psi
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Locale
import kotlin.test.assertEquals

internal class PressureTest {

    private lateinit var defaultLocale: Locale

    @Before
    fun setup() {
        defaultLocale = Locale.getDefault()
        Locale.setDefault(Locale.US)
    }

    @After
    fun tearDown() {
        Locale.setDefault(defaultLocale)
    }

    @Test
    fun `shows two bar decimals below 10 bar`() {
        assertEquals("9.99 bar", 9.99f.bar.string(BAR))
    }

    @Test
    fun `shows one bar decimal from 10 bar`() {
        assertEquals("10.3 bar", 10.34f.bar.string(BAR))
    }

    @Test
    fun `shows one bar decimal when rounding reaches 10 bar`() {
        assertEquals("10.0 bar", 9.997f.bar.string(BAR))
    }

    @Test
    fun `shows one psi decimal below 100 psi`() {
        assertEquals("99.9 psi", 99.9f.psi.string(PSI))
    }

    @Test
    fun `shows no psi decimal from 100 psi`() {
        assertEquals("150 psi", 150f.psi.string(PSI))
    }

    @Test
    fun `shows no psi decimal when rounding reaches 100 psi`() {
        assertEquals("100 psi", 99.96f.psi.string(PSI))
    }
}
