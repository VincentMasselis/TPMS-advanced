import CommitSha.Parameters
import org.gradle.api.provider.Property
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal abstract class CommitSha : ValueSource<String, Parameters> {

    interface Parameters : ValueSourceParameters {
        /**
         *  Related to https://git-scm.com/docs/git-rev-parse#_specifying_revisions
         *  Some examples:
         *  - `HEAD` for the current commit
         *  - `1.3.1` for the commit associated to the tag `1.3.1`
         *  - `main` for the latest commit in the `main`
         */
        val argument: Property<String>
    }

    @get:Inject
    protected abstract val execOperations: ExecOperations

    override fun obtain(): String = ByteArrayOutputStream()
        .also {
            execOperations.exec {
                commandLine(
                    "git",
                    "rev-parse",
                    parameters.argument.get()
                )
                standardOutput = it
            }
        }
        .use { it.toString() }
        .trimIndent()
}