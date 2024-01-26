import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

public abstract class CommitCountValueSource : ValueSource<Int, ValueSourceParameters.None> {

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun obtain(): Int = ByteArrayOutputStream()
        .also {
            execOperations.exec {
                commandLine("git", "rev-list", "--first-parent", "--count", "origin/main")
                standardOutput = it
            }
        }
        .use { it.toString() }
        .trim().toInt()
        .plus(1000)
}