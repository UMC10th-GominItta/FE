package com.gominitta.android.presentation.report

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorryThemeReportModelTest {
    @Test
    fun `3건 미만이면 차트를 렌더링하지 않는다`() {
        assertFalse(WorryThemeReportData(totalCount = 2, themes = emptyList()).canRender)
        assertTrue(WorryThemeReportData(totalCount = 3, themes = emptyList()).canRender)
    }

    @Test
    fun `모든 비율이 30퍼센트 미만이면 첫 최상위 테마만 승격한다`() {
        val data = WorryThemeReportData(
            totalCount = 10,
            themes = listOf(
                WorryThemeItem(WorryTheme.CAREER, 25),
                WorryThemeItem(WorryTheme.STUDY, 25),
                WorryThemeItem(WorryTheme.HEALTH, 0),
            ),
        )

        val ranked = data.rankedThemes()

        assertEquals(2, ranked.size)
        assertEquals(WorryThemeWeight.PRIMARY, ranked[0].weight)
        assertEquals(WorryThemeWeight.NORMAL, ranked[1].weight)
    }

    @Test
    fun `공동 1위이면 두 테마를 피드백에 표시한다`() {
        val data = WorryThemeReportData(
            totalCount = 10,
            themes = listOf(
                WorryThemeItem(WorryTheme.CAREER, 40),
                WorryThemeItem(WorryTheme.STUDY, 40),
                WorryThemeItem(WorryTheme.ETC, 20),
            ),
        )

        assertEquals(
            "최근에는 진로와 학업에 대한 고민이 깊었네요.",
            data.feedbackText(),
        )
    }
}
