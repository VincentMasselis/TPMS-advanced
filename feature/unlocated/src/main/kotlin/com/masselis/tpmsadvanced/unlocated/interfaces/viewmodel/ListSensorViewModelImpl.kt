package com.masselis.tpmsadvanced.unlocated.interfaces.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masselis.tpmsadvanced.data.unit.interfaces.UnitPreferences
import com.masselis.tpmsadvanced.unlocated.interfaces.viewmodel.ListSensorViewModel.State
import com.masselis.tpmsadvanced.unlocated.usecase.ListTyreUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class ListSensorViewModelImpl @Inject constructor(
    private val listTyreUseCase: ListTyreUseCase,
    private val unitPreferences: UnitPreferences,
) : ViewModel(), ListSensorViewModel {

    override val stateFlow = MutableStateFlow<State>(State.PlugSensor)

    private var searchJob: Job? = null

    override fun acknowledgePlugSensor() {
        if (stateFlow.value !is State.PlugSensor)
            return
        searchJob = combine(
            listTyreUseCase(),
            unitPreferences.pressure,
            unitPreferences.temperature
        ) { (readyToBinds, bounds), pressureUnit, temperatureUnit ->
            if (readyToBinds.isEmpty() && bounds.isEmpty()) State.SearchingNoResult
            else State.SearchingFoundTyre(readyToBinds, bounds, pressureUnit, temperatureUnit)
        }.catch { State.Issue }
            .onEach { stateFlow.value = it }
            .launchIn(viewModelScope)
    }

    override fun onSensorBound() {
        if (stateFlow.value !is State.Searching)
            return
        searchJob?.cancel()
        stateFlow.value = State.PlugSensor
    }
}
