import org.gradle.api.GradleException
import java.util.regex.Matcher
import java.util.regex.Pattern

public sealed interface SemanticVersion : StricSemanticVersion {

    public val prerelease: String?
    public val buildmetadata: String?

    override fun compareTo(other: StricSemanticVersion): Int {
        if (prerelease != null || buildmetadata != null)
            error("Cannot compare this version because prerelease and buildmetadata are not comparable")
        return super.compareTo(other)
    }

    public data class Impl(
        override val major: Int,
        override val minor: Int,
        override val patch: Int,
        override val prerelease: String?,
        override val buildmetadata: String?,
    ) : SemanticVersion {

        public constructor(input: String) : this(
            pattern.matcher(input).apply {
                if (matches().not())
                    throw GradleException("Filled version is not a valid semantic version, filled version: $input")
            })

        private constructor(matcher: Matcher) : this(
            matcher.group("major").toInt(),
            matcher.group("minor").toInt(),
            matcher.group("patch").toInt(),
            matcher.group("prerelease"),
            matcher.group("buildmetadata")
        )

        override fun toString(): String = StringBuilder("$major.$minor.$patch")
            .apply {
                if (prerelease != null) append("-$prerelease")
                if (buildmetadata != null) append("+$buildmetadata")
            }
            .toString()
    }

    public companion object {

        public val pattern: Pattern =
            Pattern.compile("^(?<major>0|[1-9]\\d*)\\.(?<minor>0|[1-9]\\d*)\\.(?<patch>0|[1-9]\\d*)(?:-(?<prerelease>(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+(?<buildmetadata>[0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?\$")

        public operator fun invoke(input: String): SemanticVersion = Impl(input)
        public operator fun invoke(
            major: Int,
            minor: Int,
            patch: Int,
            prerelease: String?,
            buildmetadata: String?
        ): SemanticVersion = Impl(major, minor, patch, prerelease, buildmetadata)

        public operator fun invoke(strict: StricSemanticVersion): SemanticVersion = Impl(
            strict.major,
            strict.minor,
            strict.patch,
            null,
            null
        )
    }
}