package com.masselis.tpmsadvanced.gitflow.task

import com.masselis.tpmsadvanced.gitflow.version.BumpType
import com.masselis.tpmsadvanced.gitflow.version.BumpType.Companion.bump
import com.masselis.tpmsadvanced.gitflow.version.TpmsToml
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * Writes a Play Store release note for the version about to be released, from [noteText]. Does
 * nothing when [noteText] isn't set, which is the case for every local invocation - a developer
 * creates the release note file by hand, this task exists only for `workflow_dispatch`, where a
 * human can supply the note text as a plain workflow input instead of a file.
 */
internal abstract class WriteReleaseNoteIfSupplied : DefaultTask() {

    @get:InputFile
    abstract val versionCatalog: RegularFileProperty

    @get:Input
    @get:Optional
    abstract val bumpType: Property<BumpType>

    @get:Input
    @get:Optional
    abstract val noteText: Property<String>

    @get:Internal
    abstract val releaseNotesDir: DirectoryProperty

    init {
        group = "gitflow"
        description = "Writes a Play Store release note from -Pgitflow.releaseNote, if supplied"
    }

    @TaskAction
    internal fun process() {
        val text = noteText.orNull?.takeIf(String::isNotBlank) ?: return
        val bumpType = bumpType.orNull ?: return
        val target = TpmsToml(versionCatalog.get().asFile).appVersion.bump(bumpType)
        releaseNotesDir.get().file("$target.txt").asFile.also { file ->
            file.parentFile.mkdirs()
            file.writeText(text)
            logger.lifecycle("Wrote release note for version {} to {}", target, file)
        }
    }
}
