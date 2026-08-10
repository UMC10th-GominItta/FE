package com.gominitta.android.data.repository

import com.gominitta.android.domain.model.HomeData
import com.gominitta.android.domain.model.UserProfile
import com.gominitta.android.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DummyUserRepository @Inject constructor() : UserRepository {

    private var nickname = "00님"
    private var profileImageUrl = "cat3"
    private val email = "abcdef@gmail.com"

    override suspend fun getMyProfile(): UserProfile = UserProfile(
        nickname = nickname,
        profileImageUrl = profileImageUrl,
        email = email,
    )

    override suspend fun updateNickname(nickname: String) {
        this.nickname = nickname
    }

    override suspend fun updateProfileImage(profileImageUrl: String) {
        this.profileImageUrl = profileImageUrl
    }

    override suspend fun getHome(): HomeData = HomeData(
        nickname = nickname,
        dailyMessage = "",
        nextSession = null,
    )
}