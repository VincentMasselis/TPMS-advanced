package com.masselis.tpmsadvanced.feature.main.interfaces.car

import android.content.Intent
import androidx.annotation.OptIn
import androidx.car.app.annotations.ExperimentalCarApi
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridSection
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import androidx.car.app.model.SectionedItemTemplate
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import com.masselis.tpmsadvanced.core.common.appContext
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.FRONT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Axle.REAR
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.FRONT_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.REAR_RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.LEFT
import com.masselis.tpmsadvanced.data.vehicle.model.SensorLocation.Side.RIGHT
import com.masselis.tpmsadvanced.data.vehicle.model.Tyre
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle.Kind.Location
import com.masselis.tpmsadvanced.feature.main.ioc.VehicleComponent
import com.masselis.tpmsadvanced.feature.main.R
import com.masselis.tpmsadvanced.feature.main.usecase.VehicleRangesUseCase

public fun GetLocationsForVehicle(vehicle: VehicleComponent): List<Location> {
    return when(vehicle.vehicle.kind) {
        Kind.CAR -> listOf(Location.Wheel(FRONT_LEFT), Location.Wheel(FRONT_RIGHT), Location.Wheel(REAR_LEFT), Location.Wheel(REAR_RIGHT))
        Kind.SINGLE_AXLE_TRAILER -> listOf(Location.Side(LEFT), Location.Side(RIGHT))
        Kind.MOTORCYCLE -> listOf(Location.Axle(FRONT), Location.Axle(REAR))
        Kind.TADPOLE_THREE_WHEELER -> listOf(Location.Wheel(FRONT_LEFT), Location.Wheel(FRONT_RIGHT), Location.Axle(REAR))
        Kind.DELTA_THREE_WHEELER -> listOf(Location.Axle(FRONT), Location.Wheel(REAR_LEFT), Location.Wheel(REAR_RIGHT))
    }
}

public fun Vehicle(component: VehicleComponent, tyres: Iterable<Tyre.Located>, rangesUseCase: VehicleRangesUseCase?, unitPreferences: UnitPreferences, settingsIntent: Intent): Template {
    return when (component.vehicle.kind){
        Kind.CAR -> Car(component, tyres, rangesUseCase, unitPreferences,settingsIntent)
        Kind.SINGLE_AXLE_TRAILER -> SingleAxleTrailer(component, tyres, rangesUseCase, unitPreferences)
        Kind.MOTORCYCLE -> Motorcycle(component, tyres, rangesUseCase, unitPreferences)
        Kind.TADPOLE_THREE_WHEELER -> TadpoleThreadWheeler(component, tyres, rangesUseCase, unitPreferences)
        Kind.DELTA_THREE_WHEELER -> DeltaThreeWheeler(component, tyres, rangesUseCase, unitPreferences)
    }
}

@OptIn(ExperimentalCarApi::class)
public fun Car(component: VehicleComponent, tyres: Iterable<Tyre.Located>, rangesUseCase: VehicleRangesUseCase?, unitPreferences: UnitPreferences, settingsIntent: Intent): Template {
    val flTyre = tyres.find { tyre -> tyre.location == Location.Wheel(FRONT_LEFT) }
    val frTyre = tyres.find { tyre -> tyre.location == Location.Wheel(FRONT_RIGHT) }
    val rlTyre = tyres.find { tyre -> tyre.location == Location.Wheel(REAR_LEFT) }
    val rrTyre = tyres.find { tyre -> tyre.location == Location.Wheel(REAR_RIGHT) }

    return GridTemplate.Builder().setSingleList(ItemList.Builder()
        .addItem(Tyre(flTyre, component, rangesUseCase, unitPreferences))
        .addItem(Tyre(frTyre, component, rangesUseCase, unitPreferences))
        .addItem(Tyre(rlTyre, component, rangesUseCase, unitPreferences))
        .addItem(Tyre(rrTyre, component, rangesUseCase, unitPreferences))
        .addItem(SettingsButton(settingsIntent))
        .build())
//        .addSection(GridSection.Builder().setItems(gridItems1).build())
  //      .addSection(GridSection.Builder().setItems(gridItems2).build())
        .build()
}

@OptIn(ExperimentalCarApi::class)
public fun SingleAxleTrailer(component: VehicleComponent, tyres: Iterable<Tyre.Located>, rangesUseCase: VehicleRangesUseCase?, unitPreferences: UnitPreferences): Template  {
    val lTyre = tyres.find { tyre -> tyre.location == Location.Side(LEFT) }
    val rTyre = tyres.find { tyre -> tyre.location == Location.Side(RIGHT) }

    val gridItems1 = listOf(
        Tyre(lTyre, component, rangesUseCase, unitPreferences),
        Tyre(rTyre, component, rangesUseCase, unitPreferences)
    )

    return SectionedItemTemplate.Builder()
        .addSection(GridSection.Builder().setItems(gridItems1).build())
        .build()
}

@OptIn(ExperimentalCarApi::class)
public fun Motorcycle(component: VehicleComponent, tyres: Iterable<Tyre.Located>, rangesUseCase: VehicleRangesUseCase?, unitPreferences: UnitPreferences): Template  {
    val fTyre = tyres.find { tyre -> tyre.location == Location.Axle(FRONT) }
    val rTyre = tyres.find { tyre -> tyre.location == Location.Axle(REAR) }

    val gridItems1 = listOf(
        Tyre(fTyre, component, rangesUseCase, unitPreferences),
    )

    val gridItems2 = listOf(
        Tyre(rTyre, component, rangesUseCase, unitPreferences),
    )

    return SectionedItemTemplate.Builder()
        .addSection(GridSection.Builder().setItems(gridItems1).build())
        .addSection(GridSection.Builder().setItems(gridItems2).build())
        .build()
}

@OptIn(ExperimentalCarApi::class)
public fun TadpoleThreadWheeler(component: VehicleComponent, tyres: Iterable<Tyre.Located>, rangesUseCase: VehicleRangesUseCase?, unitPreferences: UnitPreferences): Template  {
    val flTyre = tyres.find { tyre -> tyre.location == Location.Wheel(FRONT_LEFT) }
    val frTyre = tyres.find { tyre -> tyre.location == Location.Wheel(FRONT_RIGHT) }
    val rTyre = tyres.find { tyre -> tyre.location == Location.Axle(REAR) }

    val gridItems1 = listOf(
        Tyre(flTyre, component, rangesUseCase,unitPreferences),
        Tyre(frTyre, component, rangesUseCase,unitPreferences)
    )
    val gridItems2 = listOf(
        Tyre(rTyre, component, rangesUseCase,unitPreferences),
    )

    return SectionedItemTemplate.Builder()
        .addSection(GridSection.Builder().setItems(gridItems1).build())
        .addSection(GridSection.Builder().setItems(gridItems2).build())
        .build()
}

@OptIn(ExperimentalCarApi::class)
public fun DeltaThreeWheeler(component: VehicleComponent, tyres: Iterable<Tyre.Located>, rangesUseCase: VehicleRangesUseCase?, unitPreferences: UnitPreferences): Template  {
    val fTyre = tyres.find { tyre -> tyre.location == Location.Axle(FRONT) }
    val rlTyre = tyres.find { tyre -> tyre.location == Location.Wheel(REAR_LEFT) }
    val rrTyre = tyres.find { tyre -> tyre.location == Location.Wheel(REAR_RIGHT) }

    val gridItems1 = listOf(
        Tyre(fTyre, component, rangesUseCase, unitPreferences),
    )
    val gridItems2 = listOf(
        Tyre(rlTyre, component, rangesUseCase,unitPreferences),
        Tyre(rrTyre, component, rangesUseCase,unitPreferences)
    )

    return SectionedItemTemplate.Builder()
        .addSection(GridSection.Builder().setItems(gridItems1).build())
        .addSection(GridSection.Builder().setItems(gridItems2).build())
        .build()
}

public fun SettingsButton(settingsIntent: Intent): GridItem {
    return GridItem.Builder()
        .setImage(CarIcon.Builder(IconCompat.createWithResource(appContext, R.drawable.baseline_settings_24)).setTint(
            CarColor.DEFAULT).build())
        .setText("Settings")
        .setOnClickListener {
            appContext.startActivity(settingsIntent)
        }
        .build()
}