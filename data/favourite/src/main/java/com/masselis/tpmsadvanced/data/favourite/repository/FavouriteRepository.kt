package com.masselis.tpmsadvanced.data.favourite.repository

import android.content.Context
import androidx.core.content.edit
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.core.common.observableStateFlow
import com.masselis.tpmsadvanced.data.favourite.ioc.SingleInstance
import com.masselis.tpmsadvanced.data.record.model.TyreLocation
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@SingleInstance
public class FavouriteRepository @Inject constructor() {

    private val sharedPreferences = appContext.getSharedPreferences(
        "SENSOR_IDS",
        Context.MODE_PRIVATE
    )

    private val cache = ConcurrentHashMap<TyreLocation, MutableStateFlow<Int?>>()

    public fun favouriteId(location: TyreLocation): MutableStateFlow<Int?> = cache
        .computeIfAbsent(location) { key ->
            observableStateFlow(
                sharedPreferences
                    .getInt(key.name, Int.MIN_VALUE)
                    .takeIf { it != Int.MIN_VALUE }
            ) { _, newValue ->
                sharedPreferences.edit {
                    if (newValue != null) putInt(key.name, newValue.toInt())
                    else remove(key.name)
                }
            }
        }
}
