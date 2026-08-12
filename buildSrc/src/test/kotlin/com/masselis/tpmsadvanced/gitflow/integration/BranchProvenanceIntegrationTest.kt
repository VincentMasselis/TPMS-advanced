package com.masselis.tpmsadvanced.gitflow.integration

import com.masselis.tpmsadvanced.gitflow.task.AssertBranchHasNoForeignCommits
import com.masselis.tpmsadvanced.gitflow.task.AssertBranchProvenance
import com.masselis.tpmsadvanced.gitflow.testutil.GitFixture
import org.gradle.api.GradleException
import org.gradle.kotlin.dsl.register
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * Exercises `AssertBranchProvenance` and `AssertBranchHasNoForeignCommits` directly - both
 * register the real ValueSource classes (MergeBase, CommitSha, CommitsExclusiveTo, GitBranchList)
 * against real scripted git repos, proving the actual git plumbing and the branch-provenance
 * logic together, since the latter now lives inline in these two tasks.
 */
class BranchProvenanceIntegrationTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private fun GitFixture.assertProvenance(subject: String, develop: String, main: String) {
        project().tasks.register<AssertBranchProvenance>("assertBranchProvenance") {
            subjectBranch.set(subject)
            developBranch.set(develop)
            mainBranch.set(main)
        }.get().process()
    }

    private fun GitFixture.assertNoForeignCommits(subject: String, base: String) {
        project().tasks.register<AssertBranchHasNoForeignCommits>("assertBranchHasNoForeignCommits") {
            remote.set("origin")
            subjectBranch.set(subject)
            baseBranch.set(base)
        }.get().process()
    }

    @Test
    fun `release legitimately cut from develop passes both checks`() {
        val fixture = GitFixture.create(tmp.root)
        fixture.checkout("develop")
        fixture.commit("develop work")
        fixture.push("develop")
        fixture.checkoutNew("release/1.6.0")
        fixture.commit("chore: bump version")
        fixture.pushNewBranch("release/1.6.0")

        fixture.assertProvenance("origin/release/1.6.0", "origin/develop", "origin/main")
        fixture.assertNoForeignCommits("origin/release/1.6.0", "origin/develop")
    }

    @Test
    fun `release cut directly from main fails the merge-base check`() {
        val fixture = GitFixture.create(tmp.root)
        fixture.checkout("develop")
        fixture.commit("develop work")
        fixture.push("develop")
        // Mistake: branched from main instead of develop.
        fixture.checkout("main")
        fixture.checkoutNew("release/1.6.0")
        fixture.commit("chore: bump version")
        fixture.pushNewBranch("release/1.6.0")

        assertThrows(IllegalStateException::class.java) {
            fixture.assertProvenance("origin/release/1.6.0", "origin/develop", "origin/main")
        }
    }

    @Test
    fun `develop and main pointing at the same commit is reported as ambiguous`() {
        val fixture = GitFixture.create(tmp.root)
        // No divergence yet: develop and main are still the same commit.
        fixture.checkoutNew("release/1.6.0")

        assertThrows(IllegalStateException::class.java) {
            fixture.assertProvenance("origin/release/1.6.0", "origin/main", "origin/main")
        }
    }

    @Test
    fun `release cut from a feature branch fails the foreign-commit check and names the branch`() {
        val fixture = GitFixture.create(tmp.root)
        fixture.checkout("develop")
        fixture.commit("develop work")
        fixture.push("develop")
        fixture.checkoutNew("feat/x")
        fixture.commit("feature work")
        fixture.pushNewBranch("feat/x")
        // Mistake: release cut from the feature branch instead of develop.
        fixture.checkoutNew("release/1.6.0")
        fixture.pushNewBranch("release/1.6.0")

        assertThrows(GradleException::class.java) {
            fixture.assertNoForeignCommits("origin/release/1.6.0", "origin/develop")
        }.also { error -> assertTrue(error.message!!.contains("origin/feat/x")) }
    }

    @Test
    fun `hotfix cut from develop instead of main fails the foreign-commit check`() {
        val fixture = GitFixture.create(tmp.root)
        fixture.checkout("develop")
        fixture.commit("unreleased develop work")
        fixture.push("develop")
        // Mistake: hotfix cut from develop instead of main - ships unreleased work to production.
        fixture.checkoutNew("hotfix/1.5.1")
        fixture.pushNewBranch("hotfix/1.5.1")

        assertThrows(GradleException::class.java) {
            fixture.assertNoForeignCommits("origin/hotfix/1.5.1", "origin/main")
        }.also { error -> assertTrue(error.message!!.contains("origin/develop")) }
    }

    @Test
    fun `an already-merged and tagged feature branch does not cause a false negative`() {
        // develop -> feat-x -> (feat-x merged into develop) -> release, with feat/x's branch ref
        // still around - the release branch legitimately forked from develop's current tip.
        val fixture = GitFixture.create(tmp.root)
        fixture.checkout("develop")
        fixture.checkoutNew("feat/x")
        fixture.commit("feature work")
        fixture.pushNewBranch("feat/x")
        fixture.checkout("develop")
        fixture.merge("feat/x")
        fixture.push("develop")
        fixture.checkoutNew("release/1.6.0")
        fixture.commit("chore: bump version")
        fixture.pushNewBranch("release/1.6.0")

        fixture.assertProvenance("origin/release/1.6.0", "origin/develop", "origin/main")
        fixture.assertNoForeignCommits("origin/release/1.6.0", "origin/develop")
    }
}
