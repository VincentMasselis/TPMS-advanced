package com.masselis.tpmsadvanced.feature.main.interfaces.car

import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.GridItem
import androidx.core.graphics.drawable.IconCompat
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.feature.main.R
import com.masselis.tpmsadvanced.feature.main.ioc.VehicleComponent
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleRangesUseCase

public fun Tyre(tyre: Tyre.Located?, vehicleComponent: VehicleComponent, rangesUseCase: VehicleRangesUseCase?, unitPreferences: UnitPreferences): GridItem {
    var tyreText = "N/A"
    var tyreTitle = "N/A"
    var tyreIcon: CarIcon = CarIcon.Builder(
        IconCompat.createWithResource(appContext, R.drawable.car_tire_alert)
    ).setTint(CarColor.RED).build()

    if(tyre != null) {
        val unit = unitPreferences.pressure.value
        val tUnit = unitPreferences.temperature.value
        tyreText = "${tyre.pressure.string(unit)} ${tyre.temperature.string(tUnit)}"
        tyreTitle = tyre.location.toString()

        if(tyre.pressure.hasPressure() &&
            rangesUseCase != null &&
            tyre.pressure.kpa in rangesUseCase.lowPressure.value.kpa..rangesUseCase.highPressure.value.kpa
            && tyre.temperature.celsius in rangesUseCase.lowTemp.value.celsius..rangesUseCase.highTemp.value.celsius
        ){
            tyreIcon = CarIcon.Builder(
                IconCompat.createWithResource(appContext, R.drawable.car_tire)
            ).setTint(CarColor.DEFAULT).build()
        }
    }

    return GridItem.Builder()
        .setImage(tyreIcon)
        .setText(tyreText)
        .setTitle(tyreTitle)
        .build()
}