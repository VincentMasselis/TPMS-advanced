package com.masselis.tpmsadvanced.gitflow.version

import StricSemanticVersion
import com.masselis.tpmsadvanced.gitflow.version.BumpType.Companion.bump
import com.masselis.tpmsadvanced.gitflow.version.BumpType.MAJOR
import com.masselis.tpmsadvanced.gitflow.version.BumpType.MINOR
import com.masselis.tpmsadvanced.gitflow.version.BumpType.PATCH
import org.gradle.api.GradleException
import org.junit.Assert.assertEquals
import org.junit.Test

class BumpTypeTest {

    private val version = StricSemanticVersion("1.5.3")

    @Test
    fun `major bump resets minor and patch`() {
        assertEquals("2.0.0", version.bump(MAJOR).toString())
    }

    @Test
    fun `minor bump resets patch only`() {
        assertEquals("1.6.0", version.bump(MINOR).toString())
    }

    @Test
    fun `patch bump only increments patch`() {
        assertEquals("1.5.4", version.bump(PATCH).toString())
    }

    @Test
    fun `parse is case-insensitive`() {
        assertEquals(MAJOR, BumpType.fromWorkflowDispatch("MAJOR"))
        assertEquals(MINOR, BumpType.fromWorkflowDispatch("Minor"))
        assertEquals(PATCH, BumpType.fromWorkflowDispatch("patch"))
    }

    @Test(expected = GradleException::class)
    fun `parse rejects an unknown value`() {
        BumpType.fromWorkflowDispatch("banana")
    }
}
