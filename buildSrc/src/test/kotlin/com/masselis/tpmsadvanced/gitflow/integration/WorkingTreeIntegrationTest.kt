package com.masselis.tpmsadvanced.gitflow.integration

import com.masselis.tpmsadvanced.gitflow.testutil.GitFixture
import com.masselis.tpmsadvanced.gitflow.valuesource.WorkingTree
import org.gradle.kotlin.dsl.from
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class WorkingTreeIntegrationTest {

    @get:Rule
    val tmp = TemporaryFolder()

    @Test
    fun `a staged-only change is reported as dirty`() {
        val fixture = GitFixture.create(tmp.root)
        fixture.stageOnly("f.txt", "staged change")

        val state = fixture.project().providers.from(WorkingTree::class).get()

        assertTrue(state.staged.contains("f.txt"))
        assertTrue(state.unstaged.isEmpty())
        assertFalse(state.isClean())
    }

    @Test
    fun `an unstaged-only change is reported separately from staged`() {
        val fixture = GitFixture.create(tmp.root)
        fixture.modifyWithoutStaging("f.txt", "unstaged change")

        val state = fixture.project().providers.from(WorkingTree::class).get()

        assertTrue(state.unstaged.contains("f.txt"))
        assertTrue(state.staged.isEmpty())
    }

    @Test
    fun `an untracked file is reported separately`() {
        val fixture = GitFixture.create(tmp.root)
        fixture.createUntracked("new-file.txt")

        val state = fixture.project().providers.from(WorkingTree::class).get()

        assertTrue(state.untracked.contains("new-file.txt"))
        assertTrue(state.staged.isEmpty())
        assertTrue(state.unstaged.isEmpty())
        assertFalse(state.isClean())
    }

    @Test
    fun `a clean checkout reports nothing dirty`() {
        val fixture = GitFixture.create(tmp.root)

        val state = fixture.project().providers.from(WorkingTree::class).get()

        assertTrue(state.isClean())
    }
}
