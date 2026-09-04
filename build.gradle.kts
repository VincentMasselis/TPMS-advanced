import com.masselis.tpmsadvanced.github.GithubExtension
import com.masselis.tpmsadvanced.github.GithubPlugin
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.gradle.internal.os.OperatingSystem
import java.util.Properties

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

tasks.register<Exec>("desktopAndroidAutoHeadUnit") {
    group = "android auto"
    description = "Launches the Android Auto Desktop Head Unit (/desktop-head-unit). " +
            "Pass `-Pandroid_auto_via_usb` to forward the `-u` (USB) flag to the binary."
    val dhuDir = file("local.properties")
        .inputStream()
        .use { Properties().apply { load(it) } }
        .getProperty("sdk.dir")
        ?.let { sdkDir -> file("$sdkDir/extras/google/auto") }
        ?: error("Missing sdk.dir property in local.properties")
    workingDir = dhuDir
    standardInput = System.`in`
    commandLine(
        "desktop-head-unit"
            .let { if (OperatingSystem.current().isWindows) "$it.exe" else it }
            .let { dhuDir.resolve(it).absolutePath }
    )
    if (providers.gradleProperty("android_auto_via_usb").isPresent)
        args("-u")
}

subprojects { apply(plugin = "detekt") }
