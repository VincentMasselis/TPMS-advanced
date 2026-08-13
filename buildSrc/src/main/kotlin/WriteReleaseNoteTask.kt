
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

public abstract class WriteReleaseNoteTask : DefaultTask() {

    @get:InputDirectory
    public abstract val releaseNotesDir: DirectoryProperty

    @get:Input
    public abstract val targetVersion: Property<StricSemanticVersion>

    @get:Input
    public abstract val noteText: Property<String>

    @get:Inject
    protected abstract val execOperations: ExecOperations

    init {
        description = "Writes a Play Store release note"
    }

    @TaskAction
    internal fun process() {
        val releaseNoteFile = releaseNotesDir.get().file("${targetVersion.get()}.txt")
        with(releaseNoteFile.asFile) {
            parentFile.mkdirs()
            writeText(noteText.get().ifBlank { error("Release note content is empty") })
        }
        execOperations.exec {
            commandLine("git", "add", releaseNoteFile.asFile.absolutePath)
        }
    }
}