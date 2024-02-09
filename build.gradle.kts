import com.masselis.tpmsadvanced.gitflow.GitflowExtension
import com.masselis.tpmsadvanced.gitflow.GitflowPlugin
import com.masselis.tpmsadvanced.github.GithubExtension
import com.masselis.tpmsadvanced.github.GithubPlugin

plugins {
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.crashlytics) apply false
    alias(libs.plugins.sqldelight) apply false
}

var isDecrypted by extra(false)
try {
    apply(from = "secrets/keys.gradle")
    isDecrypted = true
    println("Project secrets decrypted")
} catch (_: Exception) {
    println("Project secrets encrypted")
}


apply<GitflowPlugin>()
configure<GitflowExtension> {
    val currentVersion = StricSemanticVersion("1.3.1")
    version = currentVersion
    developBranch = "develop"
    releaseBranch = "release/${currentVersion}"
    hotfixBranch = "hotfix/${currentVersion}"
    mainBranch = "main"
}

if (isDecrypted) {
    apply<GithubPlugin>()
    configure<GithubExtension> {
        val GITHUB_TOKEN: String by extra
        githubToken = GITHUB_TOKEN
        currentReleaseTag = the<GitflowExtension>().currentReleaseTag
        lastReleaseCommitSha = the<GitflowExtension>().lastReleaseCommitSha
    }
}

task<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

subprojects { apply(plugin = "detekt") }
