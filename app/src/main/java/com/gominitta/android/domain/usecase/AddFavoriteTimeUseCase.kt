package com.gominitta.android.domain.usecase

import com.gominitta.android.domain.model.mypage.FavoriteTime
import com.gominitta.android.domain.repository.FavoriteTimeRepository
import java.time.LocalTime
import javax.inject.Inject

class AddFavoriteTimeUseCase @Inject constructor(
    private val repository: FavoriteTimeRepository,
) {
    suspend operator fun invoke(label: String, startTime: LocalTime, endTime: LocalTime): FavoriteTime =
        repository.addFavoriteTime(label, startTime, endTime)
}