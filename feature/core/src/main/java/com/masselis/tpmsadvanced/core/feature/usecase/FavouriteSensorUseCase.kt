package com.masselis.tpmsadvanced.core.feature.usecase

import com.masselis.tpmsadvanced.core.feature.ioc.TyreScope
import com.masselis.tpmsadvanced.data.favourite.repository.FavouriteRepository
import com.masselis.tpmsadvanced.data.record.model.TyreLocation
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@TyreScope
internal class FavouriteSensorUseCase @Inject constructor(
    private val location: TyreLocation,
    private val favouriteRepository: FavouriteRepository,
    private val tyreUseCaseImpl: TyreUseCaseImpl,
) : TyreUseCase {

    val foundIds = tyreUseCaseImpl
        .listen()
        .map { it.id }
        .distinctUntilChanged()

    val savedId = favouriteRepository.favouriteId(location)

    override fun listen() = tyreUseCaseImpl.listen().filter {
        val favId = favouriteRepository.favouriteId(location).value ?: return@filter true
        favId == it.id
    }
}
