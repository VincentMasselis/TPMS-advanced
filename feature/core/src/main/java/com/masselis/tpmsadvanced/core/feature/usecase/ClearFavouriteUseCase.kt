package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.data.favourite.repository.FavouriteRepository
import com.masselis.tpmsadvanced.data.record.model.TyreLocation
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

internal class ClearFavouriteUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository
) {

    private val mutableStateFlows = TyreLocation
        .values()
        .map { loc -> favouriteRepository.favouriteId(loc) }

    fun clear() = mutableStateFlows.forEach { it.value = null }

    fun isClearingAllowed() = combine(mutableStateFlows) { ids -> ids.any { it != null } }
        .distinctUntilChanged()

}
