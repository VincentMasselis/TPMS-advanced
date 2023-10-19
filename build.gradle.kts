// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.set("tpmsAdvancedVersionCode", providers.of(CommitCountValueSource::class) {}.get())
}

plugins {
    id("com.google.gms.google-services") apply false
    id("com.google.firebase.crashlytics") apply false
    id("app.cash.sqldelight") apply false
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