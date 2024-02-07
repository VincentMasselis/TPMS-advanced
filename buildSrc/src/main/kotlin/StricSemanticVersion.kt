import org.gradle.api.GradleException
import java.io.Serializable

public interface StricSemanticVersion : Serializable {

    public val major: Int
    public val minor: Int
    public val patch: Int

    override fun toString(): String

    @JvmInline
    public value class Impl(private val version: SemanticVersion) :
        StricSemanticVersion by version {
        public constructor(input: String) : this(SemanticVersion.Impl(input))

        init {
            if (version.prerelease != null || version.buildmetadata != null)
                throw GradleException("Strict semantic version doesn't allow contains a prerelease or a buildmetadata, filled version: $version")
        }

        override fun toString(): String = version.toString()
    }

    public companion object {
        public operator fun invoke(input: String): StricSemanticVersion = Impl(input)

        public operator fun invoke(version: SemanticVersion): StricSemanticVersion = Impl(version)

    }
}