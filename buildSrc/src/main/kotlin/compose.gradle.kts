@file:Suppress("UnstableApiUsage")

plugins {
    id("org.jetbrains.kotlin.plugin.compose")
    id("detekt")
}

dependencies {
    "detektPlugins"(libs.compose.detekt)
}
