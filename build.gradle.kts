
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

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

subprojects { apply(plugin = "detekt") }
