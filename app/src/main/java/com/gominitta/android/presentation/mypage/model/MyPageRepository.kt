package com.gominitta.android.presentation.mypage.model

import javax.inject.Inject
import javax.inject.Singleton

interface MyPageRepository {
    fun getNickname(): String
    fun getEmail(): String
    fun getFavoriteTimes(): List<FavoriteTimeUiModel>
    fun getProfileImageIndex(): Int
    fun setProfileImageIndex(index: Int)
}

@Singleton
class DummyMyPageRepository @Inject constructor() : MyPageRepository {

    private var profileImageIndex: Int = ProfileImages.DEFAULT_INDEX

    override fun getNickname(): String = "00님"

    override fun getEmail(): String = "abcdef@gmail.com"

    override fun getFavoriteTimes(): List<FavoriteTimeUiModel> = listOf(
        FavoriteTimeUiModel(
            id = 1L,
            title = "출근 전",
            startTime = TimeValue(hour = 7, minute = 30, isPm = false),
            endTime = TimeValue(hour = 8, minute = 30, isPm = false),
        ),
        FavoriteTimeUiModel(
            id = 2L,
            title = "점심시간",
            startTime = TimeValue(hour = 12, minute = 0, isPm = true),
            endTime = TimeValue(hour = 1, minute = 0, isPm = true),
        ),
        FavoriteTimeUiModel(
            id = 3L,
            title = "자기 전",
            startTime = TimeValue(hour = 10, minute = 0, isPm = true),
            endTime = TimeValue(hour = 11, minute = 0, isPm = true),
        ),
    )

    override fun getProfileImageIndex(): Int = profileImageIndex

    override fun setProfileImageIndex(index: Int) {
        profileImageIndex = index
    }
}