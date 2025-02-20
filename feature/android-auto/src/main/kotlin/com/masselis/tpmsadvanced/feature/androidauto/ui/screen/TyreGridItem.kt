package com.masselis.tpmsadvanced.feature.androidauto.ui.screen

import androidx.car.app.Screen
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.GridItem
import androidx.core.graphics.drawable.IconCompat.createWithResource
import com.masselis.tpmsadvanced.feature.androidauto.R

private val Screen.tyreOkIcon: CarIcon
    get() = CarIcon
        .Builder(createWithResource(carContext, R.drawable.car_tire_alert))
        .setTint(CarColor.createCustom(0xFF0E6E14.toInt(), 0xFF81DB74.toInt()))
        .build()

private val Screen.tyreIssueIcon: CarIcon
    get() = CarIcon
        .Builder(createWithResource(carContext, R.drawable.car_tire_alert))
        .setTint(CarColor.createCustom(0xFFBA1A1A.toInt(), 0xFFFFB4AB.toInt()))
        .build()

internal fun Screen.TyreGridItem() = GridItem
    .Builder()
    .setTitle("Front left")
    .setText("OK")
    .setImage(tyreOkIcon)
    .build()