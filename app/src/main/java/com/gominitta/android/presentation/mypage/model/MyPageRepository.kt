package com.gominitta.android.presentation.mypage.model

/**
 * 마이페이지(유저 정보 + 즐겨찾는 시간) 데이터 소스를 추상화하는 Repository.
 * 지금은 더미데이터만 반환하며, 나중에 실제 API 연동 시 구현체만 교체하면 된다.
 */
interface MyPageRepository {
    fun getNickname(): String
    fun getEmail(): String
    fun getFavoriteTimes(): List<FavoriteTimeUiModel>
}

class DummyMyPageRepository : MyPageRepository {

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
}