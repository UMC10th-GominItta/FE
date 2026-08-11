package com.gominitta.android.data.repository

import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.data.remote.api.UsersApi
import com.gominitta.android.data.remote.dto.UserUpdateRequest
import com.gominitta.android.data.remote.safeApiCall
import com.gominitta.android.domain.model.HomeData
import com.gominitta.android.domain.model.NextSession
import com.gominitta.android.domain.model.UserProfile
import com.gominitta.android.domain.repository.UserRepository
import com.gominitta.android.presentation.mypage.toAppProfileImageUrl
import com.gominitta.android.presentation.mypage.toServerProfileIcon
import java.time.LocalDateTime
import java.time.OffsetDateTime
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

    override suspend fun getHome(): HomeData {
        val data = unwrap(safeApiCall { usersApi.getHome() })
        val session = data.mindSession
        val startedAt = parseDateTime(session?.startedAt)
        return HomeData(
            nickname = data.user?.nickname.orEmpty(),
            dailyMessage = data.dailyMessage?.content.orEmpty(),
            nextSession = if (session != null && startedAt != null) {
                NextSession(
                    sessionId = session.sessionId ?: 0L,
                    title = session.title.orEmpty(),
                    status = session.status.orEmpty(),
                    startedAt = startedAt,
                )
            } else null,
            profileImageUrl = data.user?.profileIcon?.toAppProfileImageUrl().orEmpty(),
        )
    }

    private fun parseDateTime(raw: String?): LocalDateTime? {
        if (raw.isNullOrBlank()) return null
        return runCatching { OffsetDateTime.parse(raw).toLocalDateTime() }
            .recoverCatching { LocalDateTime.parse(raw) }
            .getOrNull()
    }

    private fun <T> unwrap(result: ApiResult<T>): T = when (result) {
        is ApiResult.Success -> result.data
        is ApiResult.Error -> throw IllegalStateException(result.message)
        is ApiResult.NetworkError -> throw result.cause
    }
}