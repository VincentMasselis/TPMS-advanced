package com.masselis.tpmsadvanced.bitwarden

import org.gradle.api.provider.Property

public interface BitwardenExtension {
    public val server: Property<BitwardenServer>
    public val email: Property<String>
    public val password: Property<String>
    public val item: Property<String>
}
