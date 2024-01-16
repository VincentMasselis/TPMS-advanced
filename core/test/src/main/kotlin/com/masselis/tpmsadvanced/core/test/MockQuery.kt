package com.masselis.tpmsadvanced.core.test

import com.masselis.tpmsadvanced.core.database.QueryList
import com.masselis.tpmsadvanced.core.database.QueryOne
import com.masselis.tpmsadvanced.core.database.QueryOneOrNull
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop

public inline fun <reified T : Any> mockkQueryList(
    content: StateFlow<List<T>>
): QueryList<T> = mockk {
    every { execute() } returns content.value
    every { asFlow(any()) } returns content
    every { asChillFlow(any()) } returns content.drop(1)
    every { asStateFlow(any(), any()) } returns content
}

public inline fun <reified T : Any> mockkQueryList(
    content: List<T>
): QueryList<T> = mockkQueryList(MutableStateFlow(content))

public inline fun <reified T : Any> mockkQueryOne(
    content: StateFlow<T>
): QueryOne<T> = mockk {
    every { execute() } returns content.value
    every { asFlow(any()) } returns content
    every { asChillFlow(any()) } returns content.drop(1)
    every { asStateFlow(any(), any()) } returns content
}

public inline fun <reified T : Any> mockkQueryOne(
    content: T
): QueryOne<T> = mockkQueryOne(MutableStateFlow(content))

public inline fun <reified T : Any> mockkQueryOneOrNull(
    content: StateFlow<T?>
): QueryOneOrNull<T> = mockk {
    every { execute() } returns content.value
    every { asFlow(any()) } returns content
    every { asChillFlow(any()) } returns content.drop(1)
    every { asStateFlow(any(), any()) } returns content
}

public inline fun <reified T : Any> mockkQueryOneOrNull(
    content: T?
): QueryOneOrNull<T> = mockkQueryOneOrNull(MutableStateFlow(content))
