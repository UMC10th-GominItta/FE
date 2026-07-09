package com.gominitta.android.presentation.main

import androidx.annotation.DrawableRes
import com.gominitta.android.R
import com.gominitta.android.navigation.Routes

/** 하단 탭 바의 4개 탭 정의 (라우트 · 아이콘 · 라벨). */
enum class BottomNavItem(
    val route: String,
    @DrawableRes val icon: Int,
    val label: String,
) {
    HOME(Routes.HOME, R.drawable.ic_home, "홈"),
    SESSION(Routes.SESSION_LIST, R.drawable.ic_session, "마음 세션"),
    RECIPE(Routes.RECIPE, R.drawable.ic_recipe, "마음 레시피"),
    REPORT(Routes.REPORT, R.drawable.ic_report, "리포트"),
    ;

    companion object {
        val items = entries
    }
}
