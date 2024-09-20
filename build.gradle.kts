
import com.masselis.tpmsadvanced.emulator.EmulatorExtension
import com.masselis.tpmsadvanced.emulator.EmulatorPlugin
import com.masselis.tpmsadvanced.github.GithubExtension
import com.masselis.tpmsadvanced.github.GithubPlugin

plugins {
    gitflow
}

gitflow {
    version = libs.versions.app.map { StricSemanticVersion(it) }
    developBranch = "origin/develop"
    releaseBranch = version.map { "origin/release/${it}" }
    hotfixBranch = version.map { "origin/hotfix/${it}" }
    mainBranch = "origin/main"
}

var isDecrypted by extra(false)
try {
    apply(from = "secrets/keys.gradle")
    isDecrypted = true
    println("Project secrets decrypted")
} catch (_: Exception) {
    println("Project secrets encrypted")
}

if (isDecrypted) {
    apply<GithubPlugin>()
    configure<GithubExtension> {
        val GITHUB_TOKEN: String by extra
        githubToken = GITHUB_TOKEN
        currentReleaseTag = gitflow.currentReleaseTag
        lastReleaseCommitSha = gitflow.lastReleaseCommitSha
    }
}

if (isCI) {
    // Why working with a custom emulator gradle plugin while gradle-managed devices exist into the
    // AGP ? Because gradle-managed emulators are bound to the lifecycle of the gradle task which
    // executes the tests. Because of this, its impossible to fetch screenshots in the end of the
    // tests since the emulator is stopped and deleted once the tests are done.
    apply<EmulatorPlugin>()
    configure<EmulatorExtension> {
        emulatorPackage = libs.versions.garunner.emulator.get()
    }
}

task<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

subprojects { apply(plugin = "detekt") }
