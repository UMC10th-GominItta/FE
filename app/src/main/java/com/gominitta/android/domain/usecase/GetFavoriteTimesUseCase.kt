package com.gominitta.android.domain.usecase

import com.gominitta.android.domain.model.mypage.FavoriteTime
import com.gominitta.android.domain.repository.FavoriteTimeRepository
import javax.inject.Inject

class GetFavoriteTimesUseCase @Inject constructor(
    private val repository: FavoriteTimeRepository,
) {
    suspend operator fun invoke(): List<FavoriteTime> = repository.getFavoriteTimes()
}