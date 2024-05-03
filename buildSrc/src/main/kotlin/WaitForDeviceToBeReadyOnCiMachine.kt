import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.submit
import org.gradle.process.ExecOperations
import org.gradle.workers.WorkerExecutor
import java.time.Duration
import javax.inject.Inject

public abstract class WaitForDeviceToBeReadyOnCiMachine : DefaultTask() {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    @get:Inject
    protected abstract val workerExecutor: WorkerExecutor

    @get:InputFile
    public abstract val adbExecutable: RegularFileProperty


    init {
        group = "verification"
        description = "Wait for an android device run on a C.I. machine"
        timeout = Duration.ofMinutes(10)
    }

    @TaskAction
    internal fun process() {
        /*if (System.getenv("CI") == "true") {
            workerExecutor.noIsolation().submit(WaitForDeviceWorker::class) {
                adbExecutable = this@WaitForDeviceToBeReadyOnCiMachine.adbExecutable
            }
        }*/
    }
}
