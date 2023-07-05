@file:Suppress("UNSUPPORTED_FEATURE")

package com.masselis.tpmsadvanced.publisher

import com.google.api.services.androidpublisher.AndroidPublisher.Edits
import com.google.api.services.androidpublisher.model.AppEdit
import com.google.api.services.androidpublisher.model.Track
import com.masselis.tpmsadvanced.publisher.editsLock
import kotlin.concurrent.withLock

context(ServiceHolder)
internal fun <T> Edits.withEdit(
    packageName: String,
    content: AppEdit? = null,
    block: Edits.(AppEdit) -> T
): T = editsLock.withLock   {
    val edit = insert(packageName, content).execute()
    val result = block(edit)
    commit(packageName, edit.id).execute()
    result
}

internal fun Edits.updateTrack(
    packageName: String,
    editId: String,
    track: String,
    block: Track.() -> Unit
) {
    tracks()
        .get(packageName, editId, track)
        .execute()
        .also(block)
        .let { tracks().update(packageName, editId, track, it) }
        .execute()
}
