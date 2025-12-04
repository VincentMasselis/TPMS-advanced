package com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.impl

import androidx.lifecycle.ViewModel
import com.masselis.tpmsadvanced.feature.main.interfaces.viewmodel.TyreIconViewModel
import com.masselis.tpmsadvanced.feature.main.usecase.TyreIconStateFlow

internal class TyreIconViewModelImpl(
    override val stateFlow: TyreIconStateFlow,
) : ViewModel(), TyreIconViewModel
