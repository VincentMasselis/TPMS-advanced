package com.masselis.tpmsadvanced.usecase

import android.content.Context
import androidx.core.content.edit
import com.masselis.tpmsadvanced.ioc.SingleInstance
import com.masselis.tpmsadvanced.model.Record
import com.masselis.tpmsadvanced.model.TyreLocation
import com.masselis.tpmsadvanced.tools.ObservableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

@SingleInstance
class FavouriteSensorUseCase @Inject constructor(
    tyreLocation: TyreLocation,
    context: Context,
    private val sensorByteArrayUseCaseImpl: RecordUseCaseImpl,
) : RecordUseCase {

    private val key = "${tyreLocation.name}_ID"

    private val sharedPreferences = context.getSharedPreferences(
        "SENSOR_IDS",
        Context.MODE_PRIVATE
    )

    val foundIds = sensorByteArrayUseCaseImpl
        .listen()
        .map { it.intId() }
        .distinctUntilChanged()

    val savedId = ObservableStateFlow(
        sharedPreferences
            .getInt(key, Int.MIN_VALUE)
            .takeIf { it != Int.MIN_VALUE }
    ) { _, newValue ->
        sharedPreferences.edit {
            if (newValue != null) putInt(key, newValue.toInt())
            else remove(key)
        }
    }

    override fun listen() = sensorByteArrayUseCaseImpl.listen().filter {
        val favId = savedId.value
        if (favId != null) it.intId() == favId
        else true
    }

    private fun Record.intId() = ByteBuffer
        .wrap(byteArrayOf(0x00) + this.id())
        .order(ByteOrder.LITTLE_ENDIAN)
        .int
}