package com.masselis.tpmsadvanced.core.androidtest

import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed

public fun SemanticsNodeInteraction.check(
    matcher: SemanticsMatcher,
    messagePrefixOnError: (() -> String)? = null
): Boolean = try {
    assert(matcher, messagePrefixOnError)
    true
} catch (_: AssertionError) {
    false
}

public fun SemanticsNodeInteraction.isDisplayed(): Boolean = try {
    assertIsDisplayed()
    true
} catch (_: AssertionError) {
    false
}
