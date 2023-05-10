plugins {
    id("io.gitlab.arturbosch.detekt")
}

detekt {
    buildUponDefaultConfig = true
    @Suppress("DEPRECATION")
    config = files("${rootProject.rootDir}/gradle/detekt-config.yml")
}
