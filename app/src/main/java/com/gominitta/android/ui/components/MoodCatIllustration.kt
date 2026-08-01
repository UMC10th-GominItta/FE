package com.gominitta.android.ui.components

import com.gominitta.android.R

/**
 * 0~10 점수를 구간별 고양이 일러스트 리소스로 매핑 (0 / 1~2 / 3~4 / 5~6 / 7~8 / 9~10).
 * 걱정 강도(WorryIntensityScreen)·감정 점수(SessionRatingScreen) 양쪽에서 같이 쓴다.
 */
fun moodCatDrawableRes(score: Int): Int = when (score.coerceIn(0, 10)) {
    0 -> R.drawable.worry_cat_0
    1, 2 -> R.drawable.worry_cat_1_2
    3, 4 -> R.drawable.worry_cat_3_4
    5, 6 -> R.drawable.worry_cat_5_6
    7, 8 -> R.drawable.worry_cat_7_8
    else -> R.drawable.worry_cat_9_10
}
