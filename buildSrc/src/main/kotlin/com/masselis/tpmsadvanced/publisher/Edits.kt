package com.masselis.tpmsadvanced.publisher

import com.google.api.services.androidpublisher.AndroidPublisher.Edits
import com.google.api.services.androidpublisher.model.AppEdit

internal fun <T> Edits.withCommit(
    packageName: String,
    content: AppEdit? = null,
    block: (AppEdit) -> T
): T {
    val edit = insert(packageName, content).execute()
    val result = block(edit)
    commit(packageName, edit.id).execute()
    return result
}