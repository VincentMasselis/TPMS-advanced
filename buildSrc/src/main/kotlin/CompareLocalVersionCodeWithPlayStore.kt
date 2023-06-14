import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

public abstract class CompareLocalVersionCodeWithPlayStore : DefaultTask() {

    @get:InputFile
    public abstract val availableVersionCodeFile: RegularFileProperty

    @get:Input
    public abstract val currentVc: Property<Int>

    init {
        group = "publishing"
        description = "Ensure the artifact to be promoted by promoteArtifact will be generated from the current commit"
    }

    @TaskAction
    internal fun process() {
        val playStoreVc = availableVersionCodeFile
            .asFile
            .get()
            .readText()
            .trim()
            .toInt() - 1
        assert(playStoreVc == currentVc.get())
    }
}