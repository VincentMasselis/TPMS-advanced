package com.masselis.tpmsadvanced.qrcode.usecase

import com.masselis.tpmsadvanced.data.favourite.repository.FavouriteRepository
import com.masselis.tpmsadvanced.data.record.model.TyreLocation
import com.masselis.tpmsadvanced.qrcode.model.SensorIds
import javax.inject.Inject

internal class SaveIdsUseCase @Inject constructor(
    private val favouriteRepository: FavouriteRepository
) {
    fun save(ids: SensorIds) {
        TyreLocation.values().forEach { loc ->
            favouriteRepository.favouriteId(loc).value = ids[loc]
        }
    }
}
