// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.set("tpmsAdvancedVersionCode", providers.of(CommitCountValueSource::class) {}.get())
}

plugins {
    id("com.android.application") apply false
    id("com.android.library") apply false
    id("org.jetbrains.kotlin.android") apply false
    id("org.jetbrains.kotlin.kapt") apply false
    id("com.google.gms.google-services") apply false
    id("com.google.firebase.crashlytics") apply false
    id("app.cash.sqldelight") apply false
    id("io.gitlab.arturbosch.detekt")
    id("dev.shreyaspatil.compose-compiler-report-generator") apply false
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}

var isDecrypted by extra(false)
try {
    apply(from = "secrets/keys.gradle")
    isDecrypted = true
    println("Project secrets decrypted")
} catch (_: Exception) {
    println("Project secrets encrypted")
}

apply(plugin = "gittag")
subprojects { apply(plugin = "detekt") }