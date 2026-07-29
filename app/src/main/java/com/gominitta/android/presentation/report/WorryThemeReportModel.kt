package com.gominitta.android.presentation.report

import com.gominitta.android.ui.components.DateRangeOption

/** API의 걱정 테마 응답을 화면에 전달하기 위한 계약입니다. */
data class WorryThemeReportData(
    val totalCount: Int,
    val themes: List<WorryThemeItem>,
) {
    val canRender: Boolean get() = totalCount >= MINIMUM_WORRY_COUNT

    companion object {
        const val MINIMUM_WORRY_COUNT = 3
    }
}

data class WorryThemeItem(
    val theme: WorryTheme,
    val percentage: Int,
)

enum class WorryTheme(val label: String) {
    CAREER("진로"),
    EMPLOYMENT("취업"),
    STUDY("학업"),
    MONEY("돈"),
    FAMILY("가족"),
    RELATIONSHIP("관계"),
    HEALTH("건강"),
    ETC("기타"),
}

internal enum class WorryThemeWeight { PRIMARY, NORMAL, MINOR }

internal data class RankedWorryTheme(
    val item: WorryThemeItem,
    val weight: WorryThemeWeight,
)

/** API 배열 순서를 동률 우선순위로 유지합니다. */
internal fun WorryThemeReportData.rankedThemes(): List<RankedWorryTheme> {
    val visible = themes
        .filter { it.percentage > 0 }
        .sortedWith(compareByDescending<WorryThemeItem> { it.percentage })
    val shouldPromoteFirst = visible.none { it.percentage >= 30 }

    return visible.mapIndexed { index, item ->
        val weight = when {
            item.percentage >= 30 -> WorryThemeWeight.PRIMARY
            shouldPromoteFirst && index == 0 -> WorryThemeWeight.PRIMARY
            item.percentage >= 10 -> WorryThemeWeight.NORMAL
            else -> WorryThemeWeight.MINOR
        }
        RankedWorryTheme(item, weight)
    }
}

internal fun WorryThemeReportData.feedbackText(): String {
    val visible = themes.filter { it.percentage > 0 }
    val highest = visible.maxOfOrNull { it.percentage } ?: return ""
    val leaders = visible.filter { it.percentage == highest }
    return if (leaders.size == 1) {
        "최근에는 ${leaders.first().theme.label}과 관련된 걱정이 가장 많았어요."
    } else {
        val names = leaders.take(2).joinToString("와 ") { it.theme.label }
        "최근에는 ${names}에 대한 고민이 깊었네요."
    }
}

/** API 연결 전 UT에서 기간 필터와 8개 테마 노출을 확인하기 위한 데이터입니다. */
internal fun worryThemeDummyData(range: DateRangeOption): WorryThemeReportData {
    val percentages = when (range) {
        DateRangeOption.LAST_2_WEEKS -> listOf(29, 21, 17, 13, 8, 6, 4, 2)
        DateRangeOption.LAST_30_DAYS -> listOf(32, 20, 16, 12, 8, 5, 4, 3)
        DateRangeOption.LAST_60_DAYS -> listOf(30, 22, 16, 11, 8, 6, 4, 3)
    }
    return WorryThemeReportData(
        totalCount = 100,
        themes = WorryTheme.entries.mapIndexed { index, theme ->
            WorryThemeItem(theme, percentages[index])
        },
    )
}
