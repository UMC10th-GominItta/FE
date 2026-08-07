package com.gominitta.android.domain.usecase

import com.gominitta.android.domain.model.mypage.FavoriteTime
import com.gominitta.android.domain.repository.FavoriteTimeRepository
import javax.inject.Inject

class UpdateFavoriteTimeUseCase @Inject constructor(
    private val repository: FavoriteTimeRepository,
) {
    suspend operator fun invoke(favoriteTime: FavoriteTime) = repository.updateFavoriteTime(favoriteTime)
}