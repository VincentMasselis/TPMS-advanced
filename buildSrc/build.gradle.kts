plugins {
    `kotlin-dsl`
}

dependencies {
    val agpVersion: String by project
    val kotlinVersion: String by project
    implementation("com.android.tools.build:gradle:$agpVersion")
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.0-RC3")
}