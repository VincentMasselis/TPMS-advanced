
import com.masselis.tpmsadvanced.bitwarden.BitwardenPlugin
import com.masselis.tpmsadvanced.github.GithubExtension
import com.masselis.tpmsadvanced.github.GithubPlugin

plugins {
    gitflow
    `monitor-resource`
}

gitflow {
    version = libs.versions.app.map { StricSemanticVersion(it) }
    developBranch = "origin/develop"
    releaseBranch = version.map { "origin/release/${it}" }
    hotfixBranch = version.map { "origin/hotfix/${it}" }
    mainBranch = "origin/main"
}

apply<BitwardenPlugin>()
val keys = extra.getOrNull<Keys>("keys")

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
