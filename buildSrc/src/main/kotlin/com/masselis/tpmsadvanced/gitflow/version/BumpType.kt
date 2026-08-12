package com.masselis.tpmsadvanced.gitflow.version

import StricSemanticVersion
import org.gradle.api.GradleException

internal enum class BumpType {
    MAJOR,
    MINOR,
    PATCH;

    companion object {
        fun StricSemanticVersion.bump(type: BumpType): StricSemanticVersion = when (type) {
            MAJOR -> StricSemanticVersion(major + 1, 0, 0)
            MINOR -> StricSemanticVersion(major, minor + 1, 0)
            PATCH -> StricSemanticVersion(major, minor, patch + 1)
        }

        fun fromWorkflowDispatch(raw: String): BumpType = when (raw.trim().lowercase()) {
            "major" -> MAJOR
            "minor" -> MINOR
            "patch" -> PATCH
            else -> throw GradleException("Unknown value \"$raw\" for -Pgitflow.bump, expected one of: major, minor, patch")
        }
    }
}
