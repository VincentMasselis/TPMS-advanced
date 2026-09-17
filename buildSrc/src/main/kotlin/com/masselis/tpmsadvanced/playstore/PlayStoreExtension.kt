package com.masselis.tpmsadvanced.playstore

import StricSemanticVersion
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

public interface PlayStoreExtension {
    public val releaseNotesDir: DirectoryProperty
    public val version: Property<StricSemanticVersion>
    public val serviceAccountCredentials: RegularFileProperty

    /**
     * Set to `false` to automatically send the artifacts for a review, use `true` otherwise. This
     * property can also be updated with a Gradle property `playStore.changesNotSentForReview`.
     *
     * This variable is useful in case of app rejection since, right after being rejected, the play
     * store forces developers to set [changesNotSentForReview] to `true` and requires the
     * developers to open the play store console and manually ask for a review.
     */
    public val changesNotSentForReview: Property<Boolean>
}