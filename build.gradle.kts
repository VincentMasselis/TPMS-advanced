import com.masselis.tpmsadvanced.gitflow.GitflowExtension
import com.masselis.tpmsadvanced.gitflow.GitflowPlugin
import com.masselis.tpmsadvanced.github.GithubExtension
import com.masselis.tpmsadvanced.github.GithubPlugin

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.set("tpmsAdvancedVersionName", SemanticVersion("1.3.1"))
}

plugins {
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.crashlytics) apply false
    alias(libs.plugins.sqldelight) apply false
}

val tpmsAdvancedVersionName: SemanticVersion by extra

apply<GitflowPlugin>()
configure<GitflowExtension> {
    versionName = tpmsAdvancedVersionName
    developBranch = "develop"
    releaseBranch = "release/${tpmsAdvancedVersionName}"
    hotfixBranch = "hotfix/${tpmsAdvancedVersionName}"
    mainBranch = "main"
}

task<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

var isDecrypted by extra(false)
try {
    apply(from = "secrets/keys.gradle")
    isDecrypted = true
    println("Project secrets decrypted")
} catch (_: Exception) {
    println("Project secrets encrypted")
}

if(isDecrypted) {
    apply<GithubPlugin>()
    configure<GithubExtension> {
        val GITHUB_TOKEN: String by extra
        githubToken = GITHUB_TOKEN
        versionName = tpmsAdvancedVersionName
        preReleaseBranch = the<GitflowExtension>().releaseBranch
        releaseBranch = the<GitflowExtension>().mainBranch
    }
}

subprojects { apply(plugin = "detekt") }
