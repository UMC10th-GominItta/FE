package com.gominitta.android.data.repository

import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.data.remote.api.UsersApi
import com.gominitta.android.data.remote.dto.UserUpdateRequest
import com.gominitta.android.data.remote.safeApiCall
import com.gominitta.android.domain.model.UserProfile
import com.gominitta.android.domain.repository.UserRepository
import com.gominitta.android.presentation.mypage.toAppProfileImageUrl
import com.gominitta.android.presentation.mypage.toServerProfileIcon
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val usersApi: UsersApi,
) : UserRepository {

    override suspend fun getMyProfile(): UserProfile {
        val data = unwrap(safeApiCall { usersApi.getMyProfile() })
        return UserProfile(
            nickname = data.nickname.orEmpty(),
            profileImageUrl = data.profileIcon?.toAppProfileImageUrl().orEmpty(),
            email = data.email.orEmpty(),
        )
    }

    override suspend fun updateNickname(nickname: String) {
        unwrap(safeApiCall { usersApi.updateMyProfile(UserUpdateRequest(nickname = nickname)) })
    }

    override suspend fun updateProfileImage(profileImageUrl: String) {
        unwrap(
            safeApiCall {
                usersApi.updateMyProfile(UserUpdateRequest(profileIcon = profileImageUrl.toServerProfileIcon()))
            },
        )
    }

    private fun <T> unwrap(result: ApiResult<T>): T = when (result) {
        is ApiResult.Success -> result.data
        is ApiResult.Error -> throw IllegalStateException(result.message)
        is ApiResult.NetworkError -> throw result.cause
    }
}