package com.gominitta.android.navigation

/**
 * Canonical route constants for every screen in the app.
 *
 * Convention:
 *  - SCREAMING_SNAKE_CASE constants; value is the route string.
 *  - Parameterised routes follow "screen/{argName}" (see template at bottom).
 *  - Presentation code references these constants, never raw strings.
 */
object Routes {
    // 온보딩 · 인증
    const val ONBOARDING = "onboarding"
    const val LOGIN      = "login"
    const val LOGIN_COMPLETE = "login_complete"

    // 메인 (하단 4탭 + 마이페이지)
    const val MAIN         = "main"
    const val HOME         = "home"
    const val SESSION_LIST = "session_list"
    const val RECIPE       = "recipe"
    const val REPORT       = "report"
    const val MY_PAGE      = "my_page"

    // 걱정 예약 플로우
    const val WORRY_FLOW      = "worry_flow"
    const val WORRY_INPUT     = "worry_input"
    const val WORRY_INTENSITY = "worry_intensity"
    const val WORRY_SCHEDULE  = "worry_schedule"
    const val WORRY_MEMO      = "worry_memo"
    const val WORRY_SAVED     = "worry_saved"

    // 마음 세션 플로우
    const val SESSION_DETAIL   = "session_detail"
    const val SESSION_ACTIVE   = "session_active"
    const val SESSION_COMPLETE = "session_complete"
    const val SESSION_RATING   = "session_rating"

    // Template: parameterised route
    // const val ITEM_DETAIL = "item_detail/{itemId}"
    // fun itemDetailRoute(id: String) = "item_detail/$id"
}
