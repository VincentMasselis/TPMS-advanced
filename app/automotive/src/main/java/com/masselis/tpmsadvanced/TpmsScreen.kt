package com.masselis.tpmsadvanced

import android.content.Intent
import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Template
import androidx.lifecycle.lifecycleScope
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation
import com.masselis.tpmsadvanced.data.vehicle.model.Temperature
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.feature.main.interfaces.car.GetLocationsForVehicle
import com.masselis.tpmsadvanced.feature.main.interfaces.car.Vehicle
import com.masselis.tpmsadvanced.feature.main.ioc.VehicleComponent
import com.masselis.tpmsadvanced.feature.main.usecase.CurrentVehicleUseCase
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleRangesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

public class TpmsScreen @AssistedInject constructor(
    private val currentVehicleUseCase: CurrentVehicleUseCase,
    private val unitPreferences: UnitPreferences,
    @Assisted ctx: CarContext
) : Screen(ctx) {

    @AssistedFactory
    public interface Factory : (CarContext) -> TpmsScreen

    private var tyres: Iterable<Tyre.Located> = listOf()
    private var ranges: VehicleRangesUseCase? = null

    init {
        lifecycleScope.launch {
            currentVehicleUseCase.collect { vehicle ->
                combine(
                    listOf(unitPreferences.pressure,
                        unitPreferences.temperature,
                        vehicle.vehicleRangesUseCase.lowTemp,
                        vehicle.vehicleRangesUseCase.highTemp,
                        vehicle.vehicleRangesUseCase.lowPressure,
                        vehicle.vehicleRangesUseCase.highPressure
                    )
                            +
                            GetLocationsForVehicle(vehicle).map { location ->
                        vehicle.TyreComponent(location).tyreAtmosphereUseCase.listenTyre()
                    }
                ) { res -> res }
                    .collect { res ->
                        ranges = vehicle.vehicleRangesUseCase
                        tyres = res.filterIsInstance<Tyre.Located>().toList()

                        Log.e("TPMS", "${ranges?.lowPressure?.value?.kpa}..${ranges?.highPressure?.value?.kpa}")
                        invalidate()
                    }
            }
        }
    }

    override fun onGetTemplate(): Template {
        val vehicleComponent = currentVehicleUseCase.value
        val settingsIntent = Intent(appContext, SettingsActivity::class.java)
        settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        return Vehicle(vehicleComponent, tyres, ranges, unitPreferences, settingsIntent)
    }
}