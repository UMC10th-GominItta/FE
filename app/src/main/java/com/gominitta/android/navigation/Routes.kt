package com.gominitta.android.navigation

/**
 * Canonical route constants for every screen in the app.
 *
 * Convention:
 *  - Use SCREAMING_SNAKE_CASE constants.
 *  - Routes with path arguments follow the pattern "screen/{argName}".
 *  - Feature packages reference these constants; they never construct
 *    raw strings themselves.
 *
 * This is the thin abstraction layer that keeps screen code independent
 * of the navigation implementation.
 */
object Routes {
    const val HOME   = "home"
    const val DETAIL = "detail"

    // Template: add parameterised routes as the app grows.
    // const val ITEM_DETAIL = "item_detail/{itemId}"
}
