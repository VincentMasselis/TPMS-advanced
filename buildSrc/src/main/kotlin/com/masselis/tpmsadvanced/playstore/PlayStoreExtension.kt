package com.masselis.tpmsadvanced.playstore

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

public interface PlayStoreExtension {
    public val serviceAccountCredentials: RegularFileProperty
}