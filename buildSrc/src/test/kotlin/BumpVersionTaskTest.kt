import BumpVersionTask.Type.Companion.bump
import BumpVersionTask.Type.MAJOR
import BumpVersionTask.Type.MINOR
import BumpVersionTask.Type.PATCH
import org.junit.Assert.assertEquals
import org.junit.Test

class BumpVersionTaskTest {

    private val version = StricSemanticVersion("1.5.3")

    @Test
    fun `major bump resets minor and patch`() {
        assertEquals("2.0.0", version.bump(MAJOR).toString())
    }

    @Test
    fun `minor bump resets patch only`() {
        assertEquals("1.6.0", version.bump(MINOR).toString())
    }

    @Test
    fun `patch bump only increments patch`() {
        assertEquals("1.5.4", version.bump(PATCH).toString())
    }

    @Test
    fun `parse is case-insensitive`() {
        assertEquals(MAJOR, BumpVersionTask.Type.fromArgument("MAJOR"))
        assertEquals(MINOR, BumpVersionTask.Type.fromArgument("Minor"))
        assertEquals(PATCH, BumpVersionTask.Type.fromArgument("patch"))
    }

    @Test(expected = IllegalStateException::class)
    fun `parse rejects an unknown value`() {
        BumpVersionTask.Type.fromArgument("banana")
    }
}
