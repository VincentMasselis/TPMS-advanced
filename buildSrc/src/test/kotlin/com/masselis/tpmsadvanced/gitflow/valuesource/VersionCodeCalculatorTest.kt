package com.masselis.tpmsadvanced.gitflow.valuesource

import StricSemanticVersion
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test

class VersionCodeCalculatorTest {

    // major=1, minor=5, patch=3 -> 1*10_000_000 + 5*100_000 + 3*1_000 = 1_05_03_000, plus suffix
    private val version = StricSemanticVersion("1.5.3")
    private val base = 1_05_03_000
    private val providers = ProjectBuilder.builder().build().providers

    @Test
    fun `on main the suffix is 999`() {
        val code = VersionCode.compute(version, "main", "release/1.5.3", "main", providers.provider { 7 })
        assertEquals(base + 999, code)
    }

    @Test
    fun `on the release branch the suffix is the build count`() {
        val code = VersionCode.compute(version, "release/1.5.3", "release/1.5.3", "main", providers.provider { 7 })
        assertEquals(base + 7, code)
    }

    @Test
    fun `elsewhere the suffix is zero`() {
        val code = VersionCode.compute(version, "develop", "release/1.5.3", "main", providers.provider { 7 })
        assertEquals(base, code)
    }

    @Test
    fun `detached HEAD sentinel falls into the else branch`() {
        val code = VersionCode.compute(version, "", "release/1.5.3", "main", providers.provider { 7 })
        assertEquals(base, code)
    }

    @Test
    fun `stays under the Play Store maximum for a plausible version`() {
        val high = StricSemanticVersion("2.9.9")
        val code = VersionCode.compute(high, "main", "release/2.9.9", "main", providers.provider { 0 })
        assertTrue(code < 2_100_000_000)
    }
}