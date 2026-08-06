package com.gominitta.android.presentation.mypage

import com.gominitta.android.presentation.mypage.model.ProfileImages

fun String.toProfileIndex(): Int = when (this) {
    "cat1" -> 0
    "cat2" -> 1
    "cat3" -> 2
    "cat4" -> 3
    "cat5" -> 4
    else -> ProfileImages.DEFAULT_INDEX
}

fun Int.toProfileImageUrl(): String = when (this) {
    0 -> "cat1"
    1 -> "cat2"
    2 -> "cat3"
    3 -> "cat4"
    4 -> "cat5"
    else -> "cat3"
}