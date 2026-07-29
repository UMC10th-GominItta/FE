package com.gominitta.android.presentation.main

import androidx.annotation.DrawableRes
import com.gominitta.android.R
import com.gominitta.android.navigation.Routes

/** 하단 탭 바의 4개 탭 정의 (라우트 · 선택/미선택 아이콘 · 라벨). */
enum class BottomNavItem(
    val route: String,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
    val label: String,
) {
    HOME(Routes.HOME, R.drawable.ic_home, R.drawable.ic_home_inactive, "홈"),
    SESSION(Routes.SESSION_LIST, R.drawable.ic_session, R.drawable.ic_session_inactive, "마음 세션"),
    RECIPE(Routes.RECIPE, R.drawable.ic_recipe, R.drawable.ic_recipe_inactive, "마음 레시피"),
    REPORT(Routes.REPORT, R.drawable.ic_report, R.drawable.ic_report_inactive, "리포트"),
    ;

    companion object {
        val items = entries
    }
}
