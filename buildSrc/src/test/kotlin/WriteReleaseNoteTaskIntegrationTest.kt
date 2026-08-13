import com.masselis.tpmsadvanced.gitflow.testutil.GitFixture
import org.gradle.kotlin.dsl.register
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class WriteReleaseNoteTaskIntegrationTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private fun notesDir(fixture: GitFixture): File =
        File(fixture.projectDir, "release-notes").apply { mkdirs() }

    @Test
    fun `write task creates the release note file from the supplied text`() {
        val fixture = GitFixture.create(tmp.root)
        val notesDir = notesDir(fixture)
        val task = fixture.project().tasks.register<WriteReleaseNoteTask>("writeReleaseNote") {
            targetVersion.set(StricSemanticVersion("1.5.1"))
            noteText.set("Fixed a crash on startup.")
            releaseNotesDir.set(notesDir)
        }.get()

        task.process()

        assertTrue(File(notesDir, "1.5.1.txt").readText() == "Fixed a crash on startup.")
    }

    @Test
    fun `write task fails when the note text is blank`() {
        val fixture = GitFixture.create(tmp.root)
        val notesDir = notesDir(fixture)
        val task = fixture.project().tasks.register<WriteReleaseNoteTask>("writeReleaseNote") {
            targetVersion.set(StricSemanticVersion("1.5.1"))
            noteText.set("   ")
            releaseNotesDir.set(notesDir)
        }.get()

        assertThrows(IllegalStateException::class.java) { task.process() }
    }
}
