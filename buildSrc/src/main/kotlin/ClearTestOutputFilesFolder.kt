import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

public abstract class ClearTestOutputFilesFolder : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:InputFile
    public abstract val adbExecutable: RegularFileProperty

    init {
        description = "Clears the phone\'s test output files folder"
    }

    @TaskAction
    internal fun process() {
        execOperations.exec {
            commandLine(
                adbExecutable.asFile.get(),
                "shell",
                "rm -rf /sdcard/googletest/test_outputfiles"
            )
        }
    }
}