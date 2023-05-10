plugins {
    `kotlin-dsl`
}

dependencies {
    // android gradle plugin, required by custom plugin
    implementation("com.android.tools.build:gradle:8.0.1") // TODO

    // kotlin plugin, required by custom plugin
    implementation(kotlin("gradle-plugin", "1.8.21")) // TODO

    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.0-RC3")
}