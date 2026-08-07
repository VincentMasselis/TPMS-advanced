package com.masselis.tpmsadvanced.gitflow.testutil

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import java.io.File

/**
 * A real, throwaway git repo for integration tests: a bare "origin" plus a working clone, so
 * genuine remote-tracking refs (e.g. `refs/remotes/origin/develop`) exist - the
 * ancestry/merge-base/foreign-commit rules operate on those, so a single-repo fixture without a
 * real remote would test nothing.
 */
internal class GitFixture private constructor(val projectDir: File) {

    private val clone: File get() = projectDir

    /**
     * A Gradle [Project] rooted at the clone, for exercising real ValueSource/task classes.
     *
     * The Gradle user home is pointed outside the clone on purpose: `ProjectBuilder` otherwise
     * defaults it under the project directory, which then shows up as an untracked `userHome/`
     * folder inside the very git repo the working-tree-cleanliness tests are asserting against.
     */
    fun project(): Project = ProjectBuilder.builder()
        .withProjectDir(clone)
        .withGradleUserHomeDir(File(clone.parentFile, "gradle-user-home"))
        .build()

    fun commit(message: String, file: String = "f.txt"): String {
        File(clone, file).writeText(message)
        run("git", "add", file)
        run("git", "commit", "-m", message)
        return run("git", "rev-parse", "HEAD").trim()
    }

    fun branch(name: String, startPoint: String) {
        run("git", "branch", name, startPoint)
    }

    fun checkout(name: String) {
        run("git", "checkout", name)
    }

    fun checkoutNew(name: String) {
        run("git", "checkout", "-b", name)
    }

    fun checkoutDetached(rev: String) {
        run("git", "checkout", "--detach", rev)
    }

    fun merge(branch: String, message: String = "Merge $branch") {
        run("git", "merge", "--no-ff", "-m", message, branch)
    }

    fun push(vararg refs: String) {
        run("git", "push", "origin", *refs)
    }

    fun pushNewBranch(branch: String) {
        run("git", "push", "-u", "origin", branch)
    }

    fun stageOnly(file: String, content: String) {
        File(clone, file).writeText(content)
        run("git", "add", file)
    }

    fun modifyWithoutStaging(file: String, content: String) {
        File(clone, file).writeText(content)
    }

    fun createUntracked(file: String, content: String = "x") {
        File(clone, file).writeText(content)
    }

    fun rev(ref: String): String = run("git", "rev-parse", ref).trim()

    private fun run(vararg command: String): String {
        val process = ProcessBuilder(*command)
            .directory(clone)
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText()
        val exitCode = process.waitFor()
        check(exitCode == 0) { "Command \"${command.joinToString(" ")}\" failed ($exitCode):\n$output" }
        return output
    }

    companion object {
        /** A fresh repo with a single commit on `main`, `main` pushed, and `develop` branched from it. */
        fun create(root: File): GitFixture {
            val origin = File(root, "origin.git").apply { mkdirs() }
            initBare(origin)
            val cloneDir = File(root, "clone").apply { mkdirs() }
            cloneFrom(origin, cloneDir)
            val fixture = GitFixture(cloneDir)
            fixture.run("git", "config", "user.email", "test@example.com")
            fixture.run("git", "config", "user.name", "Test")
            // Cloning an empty bare repo leaves HEAD on the (unborn) default branch already -
            // `git checkout main` here would fail with "pathspec did not match" since the ref
            // doesn't exist as a real branch until the first commit lands.
            fixture.commit("initial commit")
            fixture.push("main")
            fixture.checkoutNew("develop")
            fixture.pushNewBranch("develop")
            return fixture
        }

        private fun initBare(dir: File) {
            val process = ProcessBuilder("git", "init", "--bare", "-b", "main")
                .directory(dir)
                .redirectErrorStream(true)
                .start()
            check(process.waitFor() == 0)
        }

        private fun cloneFrom(origin: File, dir: File) {
            val process = ProcessBuilder("git", "clone", origin.absolutePath, dir.absolutePath)
                .directory(dir.parentFile)
                .redirectErrorStream(true)
                .start()
            check(process.waitFor() == 0)
        }
    }
}
