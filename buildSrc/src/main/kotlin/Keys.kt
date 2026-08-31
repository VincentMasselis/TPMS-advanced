import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File

@Serializable
public data class Keys(
    @SerialName("APP_KEY_STORE_PWD")
    public val appKeyStorePwd: String,
    @SerialName("APP_KEY_ALIAS")
    public val appKeyAlias: String,
    @SerialName("GITHUB_TOKEN")
    public val githubToken: String
) : java.io.Serializable {
    public companion object {
        public fun keys(file: File): Keys? = file
            .takeIf { it.exists() }
            ?.inputStream()
            ?.use { @Suppress("OPT_IN_USAGE") Json.decodeFromStream<Keys>(it) }
    }
}
