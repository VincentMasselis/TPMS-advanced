package com.masselis.tpmsadvanced.core.androidtest

public interface EnterComposable<T> {
    public fun interface Instructions<T : EnterComposable<T>> {
        public fun T.process()
    }

    public fun <T : EnterComposable<T>> process(self: T, instructions: Instructions<T>)
}

public fun <T> onEnter(
    enters: () -> Unit,
): EnterComposable<T> = object : EnterComposable<T> {
    override fun <T : EnterComposable<T>> process(
        self: T,
        instructions: EnterComposable.Instructions<T>
    ) {
        if (enters !== placeholder)
            enters()
        with(instructions) { self.process() }
    }
}

public inline fun <reified T : EnterComposable<T>> T.process(
    instructions: EnterComposable.Instructions<T>
): Unit = process(this, instructions)
