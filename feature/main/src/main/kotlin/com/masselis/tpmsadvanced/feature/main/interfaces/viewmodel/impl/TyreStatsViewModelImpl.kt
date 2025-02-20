package com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.TyreStatsViewModel
import com.masselis.tpmsadvanced.feature.main.usecase.TyreStatsStateFlow

internal class TyreStatsViewModelImpl(
    override val stateFlow: TyreStatsStateFlow,
) : ViewModel(), TyreStatsViewModel
