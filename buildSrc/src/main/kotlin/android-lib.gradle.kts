plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
}

android {
    base(this)
    defaultConfig {
        consumerProguardFile("consumer-rules.pro")
    }
}

afterEvaluate {
    if (android.buildFeatures.compose ?: false) {
        dependencies {
            lintChecks("com.slack.lint.compose:compose-lint-checks:1.2.0")
        }
    }
}
