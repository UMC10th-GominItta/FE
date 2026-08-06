package com.gominitta.android.domain.repository

import com.gominitta.android.domain.model.mypage.User

interface UserRepository {
    suspend fun getMyProfile(): User
    suspend fun updateNickname(nickname: String)
    suspend fun updateProfileImage(profileImageUrl: String)
}