import BumpVersionTask.Type.Companion.bump
import com.masselis.tpmsadvanced.gitflow.version.TpmsToml
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

public abstract class BumpVersionTask : DefaultTask() {

    @get:Input
    public abstract val bumpType: Property<Type>

    @get:InputFile
    public abstract val versionCatalog: RegularFileProperty

    @get:Inject
    protected abstract val execOperations: ExecOperations

    init {
        description = "Bumps the version in the \"libs.versions.toml\" file"
    }

    @TaskAction
    internal fun process() {
        with(TpmsToml(versionCatalog.get().asFile)) {
            appVersion = appVersion.bump(bumpType.get())
            execOperations.exec {
                commandLine("git", "add", versionCatalog.get().asFile.absolutePath)
            }
        }
    }

    public enum class Type {
        MAJOR,
        MINOR,
        PATCH;

        public companion object {
            public fun StricSemanticVersion.bump(type: Type): StricSemanticVersion = when (type) {
                MAJOR -> StricSemanticVersion(major + 1, 0, 0)
                MINOR -> StricSemanticVersion(major, minor + 1, 0)
                PATCH -> StricSemanticVersion(major, minor, patch + 1)
            }

            public fun fromArgument(raw: String): Type = when (raw.trim().lowercase()) {
                "major" -> MAJOR
                "minor" -> MINOR
                "patch" -> PATCH
                else -> error("Unknown argument for raw: \"$raw\"")
            }
        }
    }
}