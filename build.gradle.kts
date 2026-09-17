import com.masselis.tpmsadvanced.github.GithubExtension
import Keys.Companion.keys
import com.masselis.tpmsadvanced.bitwarden.BitwardenExtension
import com.masselis.tpmsadvanced.github.GithubPlugin
import org.gradle.internal.os.OperatingSystem
import java.util.Properties

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
