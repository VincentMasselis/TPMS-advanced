package com.masselis.tpmsadvanced.core.androidtest

import com.masselis.tpmsadvanced.core.androidtest.OneOffComposable.ExitToken
import java.lang.annotation.Inherited

public interface OneOffComposable<T> {

    public fun interface Instructions<T : OneOffComposable<T>> {
        context (T)
        public fun process(): ExitToken<T>
    }

    public fun <T : OneOffComposable<T>> process(self: T, instructions: Instructions<T>)

    @Suppress("unused")
    public class ExitToken<T> internal constructor()

    // TODO Find a way to make it available only for subclasses
    public val exitToken: ExitToken<T>
}

public fun <T> oneOffComposable(
    enters: () -> Unit,
    exits: () -> Unit,
): OneOffComposable<T> = object : OneOffComposable<T> {

    override fun <T : OneOffComposable<T>> process(
        self: T,
        instructions: OneOffComposable.Instructions<T>
    ): Unit = with(self) {
        if (enters !== placeholder)
            enters()
        assert(instructions.process() === exitToken)
        if (exits !== placeholder)
            exits()
    }

    override val exitToken: ExitToken<T> = ExitToken()
}

public inline fun <reified T : OneOffComposable<T>> T.process(
    instructions: OneOffComposable.Instructions<T>
): Unit = process(this, instructions)

public val placeholder: () -> Unit = {}
