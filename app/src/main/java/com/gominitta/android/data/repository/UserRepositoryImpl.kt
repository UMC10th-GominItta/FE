package com.gominitta.android.data.repository

import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.data.remote.api.UsersApi
import com.gominitta.android.data.remote.safeApiCall
import com.gominitta.android.domain.model.UserProfile
import com.gominitta.android.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val usersApi: UsersApi,
) : UserRepository {

    override suspend fun getMyProfile(): UserProfile {
        val result = safeApiCall { usersApi.getMyProfile() }
        val data = when (result) {
            is ApiResult.Success -> result.data
            is ApiResult.Error -> throw IllegalStateException(result.message)
            is ApiResult.NetworkError -> throw result.cause
        }
        return UserProfile(nickname = data.nickname.orEmpty())
    }
}
