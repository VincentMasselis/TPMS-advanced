import org.gradle.api.Project
import org.tomlj.Toml

internal fun Project.versionsToml() = Toml
    .parse(file(rootProject.projectDir.absolutePath + "/gradle/libs.versions.toml").toPath())