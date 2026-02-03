package com.masselis.tpmsadvanced.feature.androidauto.endpoint.ui.screen

import androidx.annotation.OptIn
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.annotations.ExperimentalCarApi
import androidx.car.app.model.Action
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Tab
import androidx.car.app.model.TabContents
import androidx.car.app.model.TabTemplate
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.lifecycleScope
import com.masselis.tpmsadvanced.data.vehicle.model.Vehicle
import com.masselis.tpmsadvanced.feature.androidauto.R
import com.masselis.tpmsadvanced.feature.androidauto.di.InternalComponent.Companion.TabScreenViewModel
import com.masselis.tpmsadvanced.feature.androidauto.endpoint.ui.viewmodel.TabScreenViewModel.State
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

@Suppress("OPT_IN_USAGE")
internal class TabsScreen(carContext: CarContext) : Screen(carContext) {

    private val viewModel = TabScreenViewModel()
    private val tabsCache = mutableListOf<State.Tabs.Tab>()

    init {
        viewModel
            .stateFlow
            .debounce(300.milliseconds)
            .onEach { invalidate() }
            .launchIn(lifecycleScope)
    }

    @OptIn(ExperimentalCarApi::class)
    @Suppress("MagicNumber", "NestedBlockDepth")
    override fun onGetTemplate(): Template = when (val state = viewModel.stateFlow.value) {
        State.Loading -> MessageTemplate
            .Builder("Loading...")
            .build()

        is State.Tabs -> when (state.list.size) {

            1 -> VehicleTemplate(state.displayed)

            in 2..Int.MAX_VALUE -> TabTemplate
                .Builder(
                    object : TabTemplate.TabCallback {
                        override fun onTabSelected(tabContentId: String) {
                            viewModel.currentVehicle(UUID.fromString(tabContentId))
                        }
                    })
                .setHeaderAction(Action.APP_ICON)
                .apply {
                    // No previous entry
                    if (tabsCache.isEmpty()) {
                        when (state.list.size) {
                            // Vehicle size is below 5, so there is no need to filter out some tabs
                            in 0..4 -> state.list.sortedBy { it.vehicle.name }
                            // Vehicle size is equals to 5 or above, only the 4 first vehicles will be displayed
                            else -> state.list.sortedBy {
                                // Current vehicle is the first entry
                                if (it.vehicle.uuid == state.displayed.vehicle.uuid) ""
                                else it.vehicle.name
                            }.take(4)
                        }.also(tabsCache::addAll)
                    } else {
                        // First update the list with the latest values
                        tabsCache.replaceAll { tabCache ->
                            state.list.first { newTab -> newTab.vehicle.uuid == tabCache.vehicle.uuid }
                        }
                        if (tabsCache.none { tabCache -> tabCache.vehicle.uuid == state.displayed.vehicle.uuid })
                        // If the current vehicle is not in the tab, add it first
                            tabsCache[0] = state
                                .list
                                .first { newTab -> newTab.vehicle.uuid == state.displayed.vehicle.uuid }
                    }

                    tabsCache.forEach { tab ->
                        Tab.Builder()
                            .setContentId(tab.vehicle.uuid.toString())
                            .setTitle(tab.vehicle.name)
                            .setIcon(tab.vehicle.kind.icon)
                            .build()
                            .also(::addTab)
                    }
                }
                .setActiveTabContentId(state.displayed.vehicle.uuid.toString())
                .setTabContents(TabContents.Builder(VehicleTemplate(state.displayed)).build())
                .build()

            else -> error("Unreachable condition")
        }
    }

    private val Vehicle.Kind.icon: CarIcon
        get() = CarIcon.Builder(
            IconCompat.createWithResource(
                carContext,
                when (this) {
                    Vehicle.Kind.CAR -> R.drawable.car_hatchback
                    Vehicle.Kind.SINGLE_AXLE_TRAILER -> R.drawable.truck_trailer
                    Vehicle.Kind.MOTORCYCLE -> R.drawable.motorbike
                    Vehicle.Kind.TADPOLE_THREE_WHEELER -> R.drawable.bike_scooter_24px
                    Vehicle.Kind.DELTA_THREE_WHEELER -> R.drawable.bike_scooter_24px
                }
            )
        ).setTint(CarColor.createCustom(0xFF0E6E14.toInt(), 0xFF81DB74.toInt())).build()
}
