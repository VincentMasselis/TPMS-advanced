import WaitForDeviceWorker.Parameters
import org.gradle.api.file.RegularFileProperty
import org.gradle.process.ExecOperations
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import javax.inject.Inject

internal abstract class WaitForDeviceWorker : WorkAction<Parameters> {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun execute() {
        execOperations.exec {
            commandLine(
                parameters.adbExecutable.asFile.get(),
                "wait-for-device",
                "shell",
                "while [[ -z \$(getprop sys.boot_completed | tr -d '\\r') ]]; do sleep 1; done; input keyevent 82"
            )
        }
    }

    interface Parameters : WorkParameters {
        val adbExecutable: RegularFileProperty
    }
}