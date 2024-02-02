import org.gradle.api.GradleException
import java.io.Serializable
import java.util.regex.Pattern

public data class SemanticVersion(val input: String) : Serializable {

    public val major: Int
    public val minor: Int
    public val patch: Int
    public val prerelease: String?
    public val buildmetadata: String?

    init {
        val matcher = pattern.matcher(input)
        if (matcher.matches().not())
            throw GradleException("Filled version is not a valid semantic version, filled version: $input")
        major = matcher.group("major").toInt()
        minor = matcher.group("minor").toInt()
        patch = matcher.group("patch").toInt()
        prerelease = matcher.group("prerelease")
        buildmetadata = matcher.group("buildmetadata")
    }

    override fun toString(): String = input

    public companion object {
        public val pattern: Pattern =
            Pattern.compile("^(?<major>0|[1-9]\\d*)\\.(?<minor>0|[1-9]\\d*)\\.(?<patch>0|[1-9]\\d*)(?:-(?<prerelease>(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+(?<buildmetadata>[0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?\$")
    }
}