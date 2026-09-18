package com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.data.app.interfaces.AppPreferences
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.TyreStatsViewModel
import com.masselis.tpmsadvanced.feature.main.usecase.TyreStatsStateFlow

internal class TyreStatsViewModelImpl(
    override val stateFlow: TyreStatsStateFlow,
    appPreferences: AppPreferences,
) : ViewModel(), TyreStatsViewModel {
    override val showTimestamp = appPreferences.showTimestamp
    override val showSensorId = appPreferences.showSensorId
}
