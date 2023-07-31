import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.process.ExecOperations
import java.time.Duration
import javax.inject.Inject

public abstract class WaitForDeviceToBeReadyOnCiMachine : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:InputFile
    public abstract val adbExecutable: RegularFileProperty

    init {
        group = "verification"
        description = "Wait for an android device run on a C.I. machine"
        timeout = Duration.ofMinutes(10)
    }

    @TaskAction
    internal fun process() {
        if (System.getenv("CI") == "true") execOperations.exec {
            commandLine(
                adbExecutable.asFile.get(),
                "wait-for-device",
                "shell",
                "while [[ -z \$(getprop sys.boot_completed | tr -d '\\r') ]]; do sleep 1; done; input keyevent 82"
            )
        }
    }
}
