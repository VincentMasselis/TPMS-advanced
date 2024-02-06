import CommitSha.Parameters
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class CommitSha : ValueSource<String, Parameters> {

    @Suppress("SpellCheckingInspection")
    interface Parameters : ValueSourceParameters {
        val refname: Property<String>
    }

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun obtain(): String = ByteArrayOutputStream()
        .also {
            execOperations.exec {
                commandLine(
                    "git",
                    "rev-parse",
                    parameters.refname.get()
                )
                standardOutput = it
            }
        }
        .use { it.toString() }
        .trim()
}