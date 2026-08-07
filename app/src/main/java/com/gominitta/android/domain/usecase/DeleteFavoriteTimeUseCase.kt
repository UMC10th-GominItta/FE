package com.gominitta.android.domain.usecase

import com.gominitta.android.domain.repository.FavoriteTimeRepository
import javax.inject.Inject

class DeleteFavoriteTimeUseCase @Inject constructor(
    private val repository: FavoriteTimeRepository,
) {
    suspend operator fun invoke(favoriteTimeId: Long) = repository.deleteFavoriteTime(favoriteTimeId)
}