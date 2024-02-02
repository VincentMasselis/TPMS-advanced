import com.masselis.tpmsadvanced.gitflow.GitflowExtension
import com.masselis.tpmsadvanced.gitflow.GitflowPlugin

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.set("tpmsAdvancedVersionName", SemanticVersion("1.3.1"))
}

plugins {
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.crashlytics) apply false
    alias(libs.plugins.sqldelight) apply false
}

apply<GitflowPlugin>()
configure<GitflowExtension> {
    val tpmsAdvancedVersionName: SemanticVersion by extra
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

subprojects { apply(plugin = "detekt") }
