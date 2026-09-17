package com.masselis.tpmsadvanced.gitflow.integration

import com.masselis.tpmsadvanced.gitflow.testutil.GitFixture
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

/**
 * Proves requirement 9 end to end for the assertion tasks: the configuration cache is reused
 * across identical runs, and - the property that actually matters for a safety check - a changed
 * branch is still correctly observed even when the cache *is* reused. Every assertion task reads
 * git state from a freshly-obtained `ValueSource` inside its own `@TaskAction` (execution time,
 * always re-run since these tasks declare no outputs), rather than from a value captured once
 * during configuration - so a stale configuration-cache entry can make the *build* faster, but it
 * can never make a *safety check* observe stale git state. (Gradle's `ValueSource` contract
 * separately guarantees that values which genuinely are resolved at configuration time - like
 * `versionCode`, wired into `defaultConfig` when a real Android module is present - do bust the
 * cache on change; this test's plain, AGP-free fixture project can't exercise that path, so this
 * test targets the path that actually needs proving here.)
 */
class ConfigurationCacheIntegrationTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private fun runOn(projectDir: File, vararg args: String) = GradleRunner.create()
        .withProjectDir(projectDir)
        .withPluginClasspath()
        .withArguments(*args, "--configuration-cache")

    @Test
    fun `configuration cache is reused, but a changed branch is still correctly observed`() {
        val fixture = GitFixture.create(tmp.root)
        // developBranch/mainBranch/remote all fall back to their conventions ("develop"/"main"/
        // "origin"), matching the fixture, so no gitflow {} configuration block is needed here -
        // which conveniently avoids needing StricSemanticVersion importable in this throwaway
        // script.
        File(fixture.projectDir, "settings.gradle.kts").writeText("")
        File(fixture.projectDir, "build.gradle.kts").writeText("plugins { id(\"gitflow\") }")
        fixture.checkout("develop")

        val first = runOn(fixture.projectDir, "assertCurrentBranchIsDevelop").build()
        assertTrue("first run should succeed on develop", "BUILD SUCCESSFUL" in first.output)

        val second = runOn(fixture.projectDir, "assertCurrentBranchIsDevelop").build()
        assertTrue(
            "second run with nothing changed should reuse the configuration cache",
            "Reusing configuration cache" in second.output || "Configuration cache entry reused" in second.output,
        )

        fixture.checkoutNew("release/1.0.0")
        val third = runOn(fixture.projectDir, "assertCurrentBranchIsDevelop").buildAndFail()
        assertTrue(
            "the configuration cache may legitimately still be reused here...",
            "Reusing configuration cache" in third.output || "Configuration cache entry reused" in third.output,
        )
        assertTrue(
            "...but the task must observe the real, current branch regardless - never a value " +
                "captured once during an earlier, now-stale configuration",
            "Current branch is \"release/1.0.0\"" in third.output,
        )
    }
}
