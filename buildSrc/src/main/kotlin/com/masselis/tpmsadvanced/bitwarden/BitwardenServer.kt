package com.masselis.tpmsadvanced.bitwarden

import java.io.Serializable

@Suppress("unused")
public sealed interface BitwardenServer : Serializable {
    public val url: String

    public object BitwardenCom : BitwardenServer {
        private fun readResolve(): Any = BitwardenCom
        override val url: String = "https://vault.bitwarden.com"
    }

    public object BitwardenEu : BitwardenServer {
        private fun readResolve(): Any = BitwardenEu
        override val url: String = "https://vault.bitwarden.eu"
    }

    @JvmInline
    public value class SelfHosted(public override val url: String) : BitwardenServer    {
        init {
            require(regex.matches(url)) {
                "url is not valid. url: $url"
            }
        }

        internal companion object    {
            private val regex = Regex("^https://(?:www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b[-a-zA-Z0-9()@:%_+.~#?&/=]*$")
        }
    }
}
