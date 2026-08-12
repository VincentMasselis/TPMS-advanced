package com.masselis.tpmsadvanced.gitflow.integration

import com.masselis.tpmsadvanced.gitflow.task.AssertReleaseNoteExists
import com.masselis.tpmsadvanced.gitflow.task.WriteReleaseNoteIfSupplied
import com.masselis.tpmsadvanced.gitflow.version.BumpType
import org.gradle.kotlin.dsl.register
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ReleaseNoteTaskIntegrationTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private lateinit var catalogFile: File
    private lateinit var notesDir: File

    private fun project() = ProjectBuilder.builder().withProjectDir(tmp.root).build().also {
        catalogFile = tmp.newFile("libs.versions.toml").apply { writeText("[versions]\napp = \"1.5.0\"\n") }
        notesDir = tmp.newFolder("release-notes")
    }

    @Test
    fun `fails when the release note for the bumped version is missing`() {
        val project = project()
        val task = project.tasks.register<AssertReleaseNoteExists>("assertReleaseNoteExists") {
            versionCatalog.set(catalogFile)
            bumpType.set(BumpType.MINOR)
            releaseNotesDir.set(notesDir)
        }.get()

        val error = assertThrows(IllegalStateException::class.java) { task.process() }
        assertTrue(error.message!!.contains("1.6.0"))
    }

    @Test
    fun `fails when the release note file exists but is blank`() {
        val project = project()
        File(notesDir, "1.6.0.txt").writeText("   ")
        val task = project.tasks.register<AssertReleaseNoteExists>("assertReleaseNoteExists") {
            versionCatalog.set(catalogFile)
            bumpType.set(BumpType.MINOR)
            releaseNotesDir.set(notesDir)
        }.get()

        assertThrows(IllegalStateException::class.java) { task.process() }
    }

    @Test
    fun `passes when the release note for the bumped version exists`() {
        val project = project()
        File(notesDir, "1.6.0.txt").writeText("Bug fixes.")
        val task = project.tasks.register<AssertReleaseNoteExists>("assertReleaseNoteExists") {
            versionCatalog.set(catalogFile)
            bumpType.set(BumpType.MINOR)
            releaseNotesDir.set(notesDir)
        }.get()

        task.process()
    }

    @Test
    fun `checks the current version when no bump type is set`() {
        val project = project()
        val task = project.tasks.register<AssertReleaseNoteExists>("assertReleaseNoteExists") {
            versionCatalog.set(catalogFile)
            releaseNotesDir.set(notesDir)
        }.get()

        val error = assertThrows(IllegalStateException::class.java) { task.process() }
        assertTrue(error.message!!.contains("1.5.0"))
    }

    @Test
    fun `passes for the current version when no bump type is set and its note exists`() {
        val project = project()
        File(notesDir, "1.5.0.txt").writeText("Bug fixes.")
        val task = project.tasks.register<AssertReleaseNoteExists>("assertReleaseNoteExists") {
            versionCatalog.set(catalogFile)
            releaseNotesDir.set(notesDir)
        }.get()

        task.process()
    }

    @Test
    fun `write task creates the release note file from the supplied text`() {
        val project = project()
        val task = project.tasks.register<WriteReleaseNoteIfSupplied>("writeReleaseNote") {
            versionCatalog.set(catalogFile)
            bumpType.set(BumpType.PATCH)
            noteText.set("Fixed a crash on startup.")
            releaseNotesDir.set(notesDir)
        }.get()

        task.process()

        assertTrue(File(notesDir, "1.5.1.txt").readText() == "Fixed a crash on startup.")
    }

    @Test
    fun `write task does nothing when no text is supplied`() {
        val project = project()
        val task = project.tasks.register<WriteReleaseNoteIfSupplied>("writeReleaseNote") {
            versionCatalog.set(catalogFile)
            bumpType.set(BumpType.PATCH)
            releaseNotesDir.set(notesDir)
        }.get()

        task.process()

        assertTrue(notesDir.listFiles()?.isEmpty() != false)
    }
}
