package com.gominitta.android.domain.repository

import com.gominitta.android.domain.model.HomeData
import com.gominitta.android.domain.model.UserProfile

interface UserRepository {
    suspend fun getMyProfile(): UserProfile
    suspend fun updateNickname(nickname: String)
    suspend fun updateProfileImage(profileImageUrl: String)
    suspend fun getHome(): HomeData
}