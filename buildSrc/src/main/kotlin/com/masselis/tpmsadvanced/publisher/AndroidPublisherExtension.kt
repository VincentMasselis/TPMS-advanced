package com.masselis.tpmsadvanced.publisher

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

public interface AndroidPublisherExtension {
    public val serviceAccountCredentials: RegularFileProperty
    public val packageName: Property<String>
}