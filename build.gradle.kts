
import com.masselis.tpmsadvanced.github.GithubExtension
import com.masselis.tpmsadvanced.github.GithubPlugin
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

plugins {
    gitflow
    `monitor-resource`
    bitwarden
}

gitflow {
    version = libs.versions.app.map { StricSemanticVersion(it) }
    remote = "origin"
    developBranch = "develop"
    mainBranch = "main"
}

tasks.register<BumpVersionTask>("bumpVersion") {
    this.bumpType = providers.gradleProperty("version.bump")
        .map { BumpVersionTask.Type.fromArgument(it) }
    this.versionCatalog = layout.projectDirectory.file("gradle/libs.versions.toml")
}

val keys = file("secrets/keys.json")
    .takeIf { it.exists() }
    ?.inputStream()
    ?.use {
        @Suppress("OPT_IN_USAGE")
        Json.decodeFromStream<Keys>(it)
    }
    ?.also { println("Project secrets available") }
    ?.also { extra.set("keys", it) }
    ?: run {
        println("Project secrets are missing")
        null
    }

if (keys != null) {
    apply<GithubPlugin>()
    configure<GithubExtension> {
        githubToken = keys.githubToken
        currentReleaseTag = gitflow.currentReleaseTag
        lastReleaseCommitSha = gitflow.lastReleaseCommitSha
        backMergeSource = gitflow.mainBranch
        backMergeTarget = gitflow.developBranch
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

subprojects { apply(plugin = "detekt") }
