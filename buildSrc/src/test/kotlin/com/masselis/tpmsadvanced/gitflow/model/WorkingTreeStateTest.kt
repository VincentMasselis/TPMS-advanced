package com.masselis.tpmsadvanced.gitflow.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorkingTreeStateTest {

    @Test
    fun `clean tree is clean`() {
        val state = WorkingTreeState(emptySet(), emptySet(), emptySet())
        assertTrue(state.isClean())
    }

    @Test
    fun `staged-only change is dirty`() {
        val state = WorkingTreeState(staged = setOf("a.kt"), unstaged = emptySet(), untracked = emptySet())
        assertFalse(state.isClean())
        assertTrue(state.describeDirty().contains("staged"))
    }

    @Test
    fun `unstaged-only change is dirty`() {
        val state = WorkingTreeState(staged = emptySet(), unstaged = setOf("a.kt"), untracked = emptySet())
        assertFalse(state.isClean())
    }

    @Test
    fun `untracked-only is dirty`() {
        val state = WorkingTreeState(staged = emptySet(), unstaged = emptySet(), untracked = setOf("a.kt"))
        assertFalse(state.isClean())
        assertTrue(state.describeDirty().contains("untracked"))
    }
}
