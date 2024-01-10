package com.masselis.tpmsadvanced.interfaces.tools

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed

internal fun SemanticsNodeInteraction.check(
    matcher: SemanticsMatcher,
    messagePrefixOnError: (() -> String)? = null
) = try {
    assert(matcher, messagePrefixOnError)
    true
} catch (_: AssertionError) {
    false
}

internal fun SemanticsNodeInteraction.isDisplayed() = try {
    assertIsDisplayed()
    true
} catch (_: AssertionError) {
    false
}
