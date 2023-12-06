package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
internal class SensorToBindUseCase @Inject constructor(
    private val tyreUseCaseImpl: TyreUseCaseImpl,
    private val sensorBindingUseCase: SensorBindingUseCase,
) {
    fun search() = sensorBindingUseCase
        .boundSensor()
        .flatMapLatest { boundSensor ->
            if (boundSensor != null) flowOf(Result.AlreadyBound(boundSensor))

            tyreUseCaseImpl
                .listen()
                .map {
                    if (it.location != null) Sensor.Located(it.id, it.location!!)
                    else Sensor.Impl(it.id)
                }
                .flatMapLatest { sensor ->
                    sensorBindingUseCase.boundVehicle(sensor)
                        .map { boundVehicle ->
                            if (boundVehicle == null) Result.NewBinding(sensor)
                            else Result.DuplicateBinding(sensor, boundVehicle)
                        }
                }
                .distinctUntilChanged()
        }

    sealed interface Result {
        @JvmInline
        value class AlreadyBound(val boundSensor: Sensor.Located) : Result

        @JvmInline
        value class NewBinding(val foundSensor: Sensor) : Result

        data class DuplicateBinding(
            val foundSensor: Sensor,
            val boundVehicle: Vehicle,
        ) : Result
    }
}
