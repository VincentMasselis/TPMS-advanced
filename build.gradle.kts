import com.masselis.tpmsadvanced.emulator.EmulatorExtension
import com.masselis.tpmsadvanced.emulator.EmulatorPlugin
import com.masselis.tpmsadvanced.github.GithubExtension
import com.masselis.tpmsadvanced.github.GithubPlugin
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

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

val keys = try {
    file("secrets/keys.json")
        .inputStream()
        .use {
            @Suppress("OPT_IN_USAGE")
            Json.decodeFromStream<Keys>(it)
        }
        .also { println("Project secrets decrypted") }
        .also { extra.set("keys", it) }
} catch (_: SerializationException) {
    println("Project secrets encrypted")
    null
}

if (keys != null) {
    apply<GithubPlugin>()
    configure<GithubExtension> {
        githubToken = keys.githubToken
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
