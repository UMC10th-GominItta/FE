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

fun String.toAppProfileImageUrl(): String = when (this) {
    "CHARACTER_1" -> "cat1"
    "CHARACTER_2" -> "cat2"
    "CHARACTER_3" -> "cat3"
    "CHARACTER_4" -> "cat4"
    "CHARACTER_5" -> "cat5"
    else -> "cat3"
}

fun String.toServerProfileIcon(): String = when (this) {
    "cat1" -> "CHARACTER_1"
    "cat2" -> "CHARACTER_2"
    "cat3" -> "CHARACTER_3"
    "cat4" -> "CHARACTER_4"
    "cat5" -> "CHARACTER_5"
    else -> "CHARACTER_3"
}