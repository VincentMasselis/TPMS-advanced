package com.masselis.tpmsadvanced.gitflow.integration

import com.masselis.tpmsadvanced.gitflow.model.HeadState
import com.masselis.tpmsadvanced.gitflow.testutil.GitFixture
import com.masselis.tpmsadvanced.gitflow.valuesource.CurrentBranch
import org.gradle.kotlin.dsl.from
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class CurrentBranchIntegrationTest {

    @get:Rule
    val tmp = TemporaryFolder()

    @Test
    fun `resolves the checked-out branch even with no upstream configured`() {
        val fixture = GitFixture.create(tmp.root)
        // A freshly `checkout -b`'d branch has no upstream yet - this is exactly the case that
        // crashed the old @{u}-based lookup.
        fixture.checkoutNew("release/1.6.0")

        val head = fixture.project().providers.from(CurrentBranch::class).get()

        assertTrue(head is HeadState.OnBranch)
        assertEquals("release/1.6.0", (head as HeadState.OnBranch).branch)
    }

    @Test
    fun `resolves develop as a plain local branch`() {
        val fixture = GitFixture.create(tmp.root)
        fixture.checkout("develop")

        val head = fixture.project().providers.from(CurrentBranch::class).get()

        assertEquals(HeadState.OnBranch::class, head::class)
        assertEquals("develop", (head as HeadState.OnBranch).branch)
    }

    @Test
    fun `reports a detached HEAD instead of crashing`() {
        val fixture = GitFixture.create(tmp.root)
        val sha = fixture.rev("main")
        fixture.checkoutDetached(sha)

        val head = fixture.project().providers.from(CurrentBranch::class).get()

        assertTrue(head is HeadState.Detached)
        assertEquals(sha, (head as HeadState.Detached).sha)
    }
}
