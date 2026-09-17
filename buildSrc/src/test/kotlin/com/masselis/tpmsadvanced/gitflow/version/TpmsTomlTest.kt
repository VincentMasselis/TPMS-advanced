package com.masselis.tpmsadvanced.gitflow.version

import StricSemanticVersion
import org.gradle.api.GradleException
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class TpmsTomlTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private fun tomlFile(content: String): File =
        tmp.newFile("libs.versions.toml").apply { writeText(content) }

    private val sample = """
        [versions]
        app = "1.5.0"
        # A comment right after the version we care about
        kotlin = "2.4.10"
    """.trimIndent()

    @Test
    fun `reads the current version`() {
        assertEquals("1.5.0", TpmsToml(tomlFile(sample)).appVersion.toString())
    }

    @Test
    fun `replaces only the app line, byte-preserving everything else`() {
        val file = tomlFile(sample)
        TpmsToml(file).appVersion = StricSemanticVersion("1.6.0")
        assertEquals(
            """
                [versions]
                app = "1.6.0"
                # A comment right after the version we care about
                kotlin = "2.4.10"
            """.trimIndent() + "\n",
            file.readText(),
        )
    }

    @Test
    fun `preserves indentation and trailing content around the replaced line`() {
        val file = tomlFile("[versions]\n  app = \"1.5.0\"  # keep me\nother = \"x\"\n")
        TpmsToml(file).appVersion = StricSemanticVersion("2.0.0")
        assertEquals("  app = \"2.0.0\"  # keep me", file.readLines()[1])
    }

    @Test(expected = GradleException::class)
    fun `fails loudly when the app key is missing`() {
        TpmsToml(tomlFile("[versions]\nkotlin = \"2.4.10\"\n")).appVersion
    }

    @Test(expected = GradleException::class)
    fun `fails loudly on invalid TOML`() {
        TpmsToml(tomlFile("[versions\napp = \"1.5.0\"\n")).appVersion
    }

    @Test
    fun `does not match an unrelated key that merely contains app`() {
        val file = tomlFile("[versions]\nwebapp = \"1.0.0\"\napp = \"1.5.0\"\n")
        assertEquals("1.5.0", TpmsToml(file).appVersion.toString())
    }
}
