package com.gominitta.android.data.repository

import com.gominitta.android.domain.model.mypage.FavoriteTime
import com.gominitta.android.domain.repository.FavoriteTimeRepository
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DummyFavoriteTimeRepository @Inject constructor() : FavoriteTimeRepository {

    private val favoriteTimes = mutableListOf(
        FavoriteTime(1L, "출근 전", LocalTime.of(7, 30), LocalTime.of(8, 30)),
        FavoriteTime(2L, "점심시간", LocalTime.of(12, 0), LocalTime.of(13, 0)),
        FavoriteTime(3L, "자기 전", LocalTime.of(22, 0), LocalTime.of(23, 0)),
    )

    private var nextId = 4L

    override suspend fun getFavoriteTimes(): List<FavoriteTime> = favoriteTimes.toList()

    override suspend fun addFavoriteTime(
        label: String,
        startTime: LocalTime,
        endTime: LocalTime,
    ): FavoriteTime {
        val newItem = FavoriteTime(nextId++, label, startTime, endTime)
        favoriteTimes.add(newItem)
        return newItem
    }

    override suspend fun updateFavoriteTime(favoriteTime: FavoriteTime) {
        val index = favoriteTimes.indexOfFirst { it.id == favoriteTime.id }
        if (index != -1) favoriteTimes[index] = favoriteTime
    }

    override suspend fun deleteFavoriteTime(favoriteTimeId: Long) {
        favoriteTimes.removeAll { it.id == favoriteTimeId }
    }
}