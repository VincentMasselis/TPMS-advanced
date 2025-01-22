package com.masselis.tpmsadvanced.core.androidtest


public interface EnterExitComposable<T> {

    public fun interface Instructions<T : EnterExitComposable<T>> {
        public fun T.process(): ExitToken<T>
    }

    public fun <T : EnterExitComposable<T>> process(self: T, instructions: Instructions<T>)

    @Suppress("unused")
    public class ExitToken<T> internal constructor()

    // TODO Find a way to make it available only for subclasses
    // What if exiting the screen doesn't belong to the screen itself ? For instance when clicking
    // on the top left back button
    public val exitToken: ExitToken<T>
}

public fun <T> onEnterAndOnExit(
    enters: () -> Unit,
    exits: () -> Unit,
): EnterExitComposable<T> = object : EnterExitComposable<T> {

    override fun <T : EnterExitComposable<T>> process(
        self: T,
        instructions: EnterExitComposable.Instructions<T>
    ) {
        if (enters !== placeholder)
            enters()
        with(instructions) {
            assert(self.process() === exitToken)
        }
        if (exits !== placeholder)
            exits()
    }

    override val exitToken: EnterExitComposable.ExitToken<T> = EnterExitComposable.ExitToken()
}

public inline fun <reified T : EnterExitComposable<T>> T.process(
    instructions: EnterExitComposable.Instructions<T>
): Unit = process(this, instructions)

public val placeholder: () -> Unit = {}
