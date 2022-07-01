package com.masselis.tpmsadvanced.usecase

import android.content.Context
import androidx.core.content.edit
import com.masselis.tpmsadvanced.ioc.SingleInstance
import com.masselis.tpmsadvanced.model.TyreLocation
import com.masselis.tpmsadvanced.tools.ObservableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

@SingleInstance
class FavouriteSensorUseCase @Inject constructor(
    tyreLocation: TyreLocation,
    context: Context,
    private val sensorByteArrayUseCaseImpl: SensorByteArrayUseCaseImpl,
) : SensorByteArrayUseCase {

    private val key = "${tyreLocation.name}_ID"

    private val sharedPreferences = context.getSharedPreferences(
        "SENSOR_IDS",
        Context.MODE_PRIVATE
    )

    val foundIds = sensorByteArrayUseCaseImpl
        .listen()
        .map { it.id() }

    val savedId = ObservableStateFlow(
        sharedPreferences
            .getInt(key, Int.MIN_VALUE)
            .takeIf { it != Int.MIN_VALUE }
            ?.toShort()
    ) { _, newValue ->
        sharedPreferences.edit {
            if (newValue != null) putInt(key, newValue.toInt())
            else remove(key)
        }
    }

    override fun listen() = sensorByteArrayUseCaseImpl.listen().filter {
        val favId = savedId.value
        if (favId != null) it.id() == favId
        else true
    }

    private fun ByteArray.id() = ByteBuffer
        .wrap(copyOfRange(1, 3))
        .order(ByteOrder.LITTLE_ENDIAN)
        .short
}