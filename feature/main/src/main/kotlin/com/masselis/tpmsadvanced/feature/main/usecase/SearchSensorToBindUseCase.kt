package com.masselis.tpmsadvanced.feature.main.usecase

import com.masselis.tpmsadvanced.data.vehicle.model.Sensor
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
internal class SearchSensorToBindUseCase @Inject constructor(
    private val listenTyreUseCase: ListenTyreUseCase,
    private val sensorBindingUseCase: SensorBindingUseCase,
) : () -> Flow<SearchSensorToBindUseCase.Result> {

    override fun invoke(): Flow<Result> = sensorBindingUseCase
        .boundSensor()
        .flatMapLatest { boundSensor ->
            if (boundSensor != null)
                flowOf(Result.AlreadyBound(boundSensor))
            else
                listenTyreUseCase.listen()
                    .map { Sensor(it.sensorId, it.location) }
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
        value class AlreadyBound(val boundSensor: Sensor) : Result

        @JvmInline
        value class NewBinding(val foundSensor: Sensor) : Result

        data class DuplicateBinding(
            val foundSensor: Sensor,
            val boundVehicle: Vehicle,
        ) : Result
    }
}
