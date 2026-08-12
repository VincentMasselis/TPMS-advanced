package com.masselis.tpmsadvanced.gitflow.integration

import com.masselis.tpmsadvanced.gitflow.testutil.GitFixture
import com.masselis.tpmsadvanced.gitflow.valuesource.GitBranchList
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.from
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class GitBranchListIntegrationTest {

    @get:Rule
    val tmp = TemporaryFolder()

    @Test
    fun `excludes the symbolic origin-HEAD entry, which would otherwise break rev-list`() {
        val fixture = GitFixture.create(tmp.root)

        val branches = fixture.project().providers.from(GitBranchList::class) {
            inputFilter = "origin/*"
        }.get()

        assertFalse(branches.any { "->" in it })
        assertTrue(branches.contains("origin/develop"))
        assertTrue(branches.contains("origin/main"))
    }

    @Test
    fun `an exact branch filter requires the remote prefix to match`() {
        val fixture = GitFixture.create(tmp.root)
        fixture.checkoutNew("release/1.6.0")
        fixture.pushNewBranch("release/1.6.0")

        val bareFilter = fixture.project().providers.from(GitBranchList::class) {
            inputFilter = "release/1.6.0"
        }.get()
        val prefixedFilter = fixture.project().providers.from(GitBranchList::class) {
            inputFilter = "origin/release/1.6.0"
        }.get()

        // Regression guard: a filter without the remote prefix silently matches nothing, which is
        // exactly the bug that made the original branch-uniqueness check a no-op.
        assertTrue(bareFilter.isEmpty())
        assertTrue(prefixedFilter.contains("origin/release/1.6.0"))
    }
}
