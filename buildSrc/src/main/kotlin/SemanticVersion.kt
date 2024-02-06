import org.gradle.api.GradleException
import java.io.Serializable
import java.util.regex.Pattern

public data class SemanticVersion(val input: String) : Serializable {

    public val major: Int
    public val minor: Int
    public val patch: Int

    init {
        val matcher = pattern.matcher(input)
        if (matcher.matches().not())
            throw GradleException("Filled version is not a valid semantic version, filled version: $input")
        major = matcher.group("major").toInt()
        minor = matcher.group("minor").toInt()
        patch = matcher.group("patch").toInt()
        for (index in 0 until matcher.groupCount()) {
            when (matcher.group(index)) {
                "prerelease", "buildmetadata" -> throw GradleException("Setting \"prerelease\" and \"buildmetadata\" is illegal, theses groups and directly managed by the gradle plugins. Input: \"$input\"")
            }
        }
    }

    override fun toString(): String = input

    public companion object {
        public val pattern: Pattern =
            Pattern.compile("^(?<major>0|[1-9]\\d*)\\.(?<minor>0|[1-9]\\d*)\\.(?<patch>0|[1-9]\\d*)(?:-(?<prerelease>(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+(?<buildmetadata>[0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?\$")
    }
}