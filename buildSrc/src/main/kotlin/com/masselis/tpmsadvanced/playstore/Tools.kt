package com.masselis.tpmsadvanced.playstore

import com.google.api.services.androidpublisher.AndroidPublisher.Edits
import com.google.api.services.androidpublisher.model.AppEdit
import com.google.api.services.androidpublisher.model.Track
import kotlin.concurrent.withLock

internal fun <T> Edits.withEdit(
    serviceHolder: ServiceHolder,
    packageName: String,
    content: AppEdit? = null,
    block: Edits.(AppEdit) -> T
): T = serviceHolder.editsLock.withLock {
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
