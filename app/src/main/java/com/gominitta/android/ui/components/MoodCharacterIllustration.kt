package com.gominitta.android.ui.components

import com.gominitta.android.R


enum class MoodCharacterSet {
    Cat, Squishy, Hedgehog,
}

/** 마이페이지 프로필 이미지 인덱스(0~4, ic_profile_cat1~5) → 캐릭터 세트. */
fun Int.toMoodCharacterSet(): MoodCharacterSet = when (this) {
    0, 3 -> MoodCharacterSet.Cat
    1, 4 -> MoodCharacterSet.Squishy
    2 -> MoodCharacterSet.Hedgehog
    else -> MoodCharacterSet.Cat
}

private val MoodCharacterDrawables: Map<MoodCharacterSet, List<Int>> = mapOf(
    MoodCharacterSet.Cat to listOf(
        R.drawable.worry_cat_0,
        R.drawable.worry_cat_1_2,
        R.drawable.worry_cat_3_4,
        R.drawable.worry_cat_5_6,
        R.drawable.worry_cat_7_8,
        R.drawable.worry_cat_9_10,
    ),
    MoodCharacterSet.Squishy to listOf(
        R.drawable.worry_squishy_0,
        R.drawable.worry_squishy_1_2,
        R.drawable.worry_squishy_3_4,
        R.drawable.worry_squishy_5_6,
        R.drawable.worry_squishy_7_8,
        R.drawable.worry_squishy_9_10,
    ),
    MoodCharacterSet.Hedgehog to listOf(
        R.drawable.worry_hedgehog_0,
        R.drawable.worry_hedgehog_1_2,
        R.drawable.worry_hedgehog_3_4,
        R.drawable.worry_hedgehog_5_6,
        R.drawable.worry_hedgehog_7_8,
        R.drawable.worry_hedgehog_9_10,
    ),
)

private fun Int.toScoreBucket(): Int = when (coerceIn(0, 10)) {
    0 -> 0
    1, 2 -> 1
    3, 4 -> 2
    5, 6 -> 3
    7, 8 -> 4
    else -> 5
}

/** [profileIndex](0~4)의 캐릭터 세트에서 [score](0~10) 구간에 맞는 이미지를 반환한다. */
fun moodCharacterDrawableRes(profileIndex: Int, score: Int): Int =
    MoodCharacterDrawables.getValue(profileIndex.toMoodCharacterSet())[score.toScoreBucket()]
