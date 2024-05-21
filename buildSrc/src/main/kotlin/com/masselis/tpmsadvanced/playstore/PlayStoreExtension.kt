package com.masselis.tpmsadvanced.playstore

import StricSemanticVersion
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

public interface PlayStoreExtension {
    public val version: Property<StricSemanticVersion>
    public val serviceAccountCredentials: RegularFileProperty
}