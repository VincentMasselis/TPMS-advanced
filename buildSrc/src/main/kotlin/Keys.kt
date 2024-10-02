import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Keys(
    @SerialName("APP_KEY_STORE_PWD")
    public val appKeyStorePwd: String,
    @SerialName("APP_KEY_ALIAS")
    public val appKeyAlias: String,
    @SerialName("GITHUB_TOKEN")
    public val githubToken: String
) : java.io.Serializable
