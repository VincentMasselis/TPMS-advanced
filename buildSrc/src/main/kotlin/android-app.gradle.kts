plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
}

android {
    base(this)
}

afterEvaluate {
    if (android.buildFeatures.compose ?: false) {
        dependencies {
            lintChecks("com.slack.lint.compose:compose-lint-checks:1.2.0")
        }
    }
}
