package com.masselis.tpmsadvanced.core.androidtest

import androidx.compose.ui.test.junit4.ComposeTestRule
import java.lang.Thread.sleep

context (ComposeTestRule)
public abstract class Screen<T>(private val block: T.() -> ExitToken<T>) {

    protected val exitToken: ExitToken<T> = PrivateExitToken()

    protected fun T.runBlock(): ExitToken<T> = block()
}

public sealed interface ExitToken<T>

private class PrivateExitToken<T> : ExitToken<T>
