import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

public abstract class DownloadTestOutputFiles : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:Inject
    protected abstract val fs: FileSystemOperations

    @get:InputFile
    public abstract val adbExecutable: RegularFileProperty

    @get:OutputDirectory
    public abstract val destination: DirectoryProperty

    init {
        description = "Download the test output files folder from the phone"
    }

    @TaskAction
    internal fun process() {
        fs.delete { delete(destination) }
        execOperations.exec {
            commandLine(
                adbExecutable.asFile.get(),
                "pull",
                "/sdcard/googletest/test_outputfiles",
                destination.asFile.get()
            )
        }
    }
}