package com.gominitta.android.domain.repository

import com.gominitta.android.domain.model.mypage.FavoriteTime

interface FavoriteTimeRepository {
    suspend fun getFavoriteTimes(): List<FavoriteTime>
    suspend fun addFavoriteTime(label: String, startTime: java.time.LocalTime, endTime: java.time.LocalTime): FavoriteTime
    suspend fun updateFavoriteTime(favoriteTime: FavoriteTime)
    suspend fun deleteFavoriteTime(favoriteTimeId: Long)
}