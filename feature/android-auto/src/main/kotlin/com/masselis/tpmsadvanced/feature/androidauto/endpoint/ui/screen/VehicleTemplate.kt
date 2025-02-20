package com.masselis.tpmsadvanced.feature.androidauto.endpoint.ui.screen

import androidx.annotation.OptIn
import androidx.car.app.Screen
import androidx.car.app.annotations.ExperimentalCarApi
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import com.masselis.tpmsadvanced.feature.androidauto.endpoint.ui.viewmodel.TabScreenViewModel.State

@OptIn(ExperimentalCarApi::class)
context(screen: Screen)
@Suppress("FunctionName", "FunctionNaming")
internal fun VehicleTemplate(
    tab: State.Tabs.Tab.Displayed
) = GridTemplate
    .Builder()
    .setItemSize(GridTemplate.ITEM_SIZE_SMALL)
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
            .apply {
                tab.vehicle
                    .kind
                    .locations
                    .map { location ->
                        TyreGridItem(
                            location,
                            tab.tyres[location]!!.first,
                            tab.tyres[location]!!.second
                        )
                    }
                    .forEach(::addItem)
            }
            .build()
    )
    .build()
