plugins {
    id("io.gitlab.arturbosch.detekt")
}

detekt {
    buildUponDefaultConfig = true
    config.from("${rootProject.projectDir}/buildSrc/detekt-config.yml")
}
