package com.gominitta.android.domain.repository

import com.gominitta.android.domain.model.UserProfile

interface UserRepository {
    suspend fun getMyProfile(): UserProfile
}
