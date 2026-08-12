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

internal abstract class AssertReleaseNoteExists : DefaultTask() {

    @get:InputFile
    abstract val versionCatalog: RegularFileProperty

    @get:Input
    @get:Optional
    abstract val bumpType: Property<BumpType>

    @get:Internal
    abstract val releaseNotesDir: DirectoryProperty

    init {
        group = "gitflow"
        description = "Checks a Play Store release note exists for the version about to be released"
    }

    @TaskAction
    internal fun process() {
        TpmsToml(versionCatalog.get().asFile)
            .appVersion
            .let {
                if (bumpType.isPresent) it.bump(bumpType.get())
                else it
            }
            .also { target ->
                releaseNotesDir.get()
                    .file("$target.txt")
                    .asFile
                    .also { file ->
                        check(file.isFile && file.readText().isNotBlank()) {
                            "Missing Play Store release note for version $target, create it before continuing: " +
                                    file.absolutePath
                        }
                    }
            }
    }
}
