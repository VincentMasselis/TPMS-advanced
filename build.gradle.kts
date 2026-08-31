import Keys.Companion.keys
import com.masselis.tpmsadvanced.bitwarden.BitwardenExtension
import com.masselis.tpmsadvanced.github.GithubExtension
import com.masselis.tpmsadvanced.github.GithubPlugin

plugins {
    gitflow
    `monitor-resource`
    bitwarden
}

val keysFile = layout.projectDirectory.file("secrets/keys.json")
configure<BitwardenExtension> {
    files.put("keys.json", keysFile)
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

keys(keysFile.asFile)?.also { keys ->
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
