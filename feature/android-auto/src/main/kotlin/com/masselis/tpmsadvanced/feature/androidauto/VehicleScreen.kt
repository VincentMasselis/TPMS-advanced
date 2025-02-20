package com.masselis.tpmsadvanced.feature.androidauto

import androidx.annotation.OptIn
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.annotations.ExperimentalCarApi
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.GridTemplate.ITEM_SIZE_SMALL
import androidx.car.app.model.ItemList
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat.createWithResource

internal class VehicleScreen(carContext: CarContext) : Screen(carContext) {

    private val tyreOkIcon: CarIcon = CarIcon
        .Builder(createWithResource(carContext, R.drawable.car_tire_alert))
        .setTint(CarColor.createCustom(0xFF0E6E14.toInt(), 0xFF81DB74.toInt()))
        .build()

    private val tyreIssueIcon: CarIcon = CarIcon
        .Builder(createWithResource(carContext, R.drawable.car_tire_alert))
        .setTint(CarColor.createCustom(0xFFBA1A1A.toInt(), 0xFFFFB4AB.toInt()))
        .build()

    @OptIn(ExperimentalCarApi::class)
    override fun onGetTemplate(): Template = GridTemplate
        .Builder()
        .setItemSize(ITEM_SIZE_SMALL)
        /*.setHeader(
            Header
                .Builder()
                .setStartHeaderAction(Action.APP_ICON)
                .setTitle("Tyre monitoring")
                .build()
        )*/
        .setSingleList(
            ItemList
                .Builder()
                .addItem(
                    GridItem
                        .Builder()
                        .setTitle("Front left")
                        .setText("OK")
                        .setImage(tyreOkIcon)
                        .build()
                )
                .addItem(
                    GridItem
                        .Builder()
                        .setTitle("Front right")
                        .setText("OK")
                        .setImage(tyreOkIcon)
                        .build()
                )
                .addItem(
                    GridItem
                        .Builder()
                        .setTitle("Rear left")
                        .setText("OK")
                        .setImage(tyreIssueIcon)
                        .build()
                )
                .addItem(
                    GridItem
                        .Builder()
                        .setTitle("Rear right")
                        .setText("OK")
                        .setImage(tyreIssueIcon)
                        .build()
                )
                .build()
        )
        .build()
}