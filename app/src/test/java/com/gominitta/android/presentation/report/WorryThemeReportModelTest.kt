package com.gominitta.android.presentation.report

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.hypot

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

    @Test
    fun `일반적인 8개 테마는 카드 안에서 겹치지 않게 배치한다`() {
        val data = worryThemeDummyData(
            com.gominitta.android.ui.components.DateRangeOption.LAST_30_DAYS,
        )
        val placements = layoutWorryThemeBubbles(data.rankedThemes())

        assertEquals(8, placements.size)
        placements.forEach { placement ->
            assertTrue(placement.x >= 0f)
            assertTrue(placement.y >= 0f)
            assertTrue(placement.x + placement.size <= 319f)
            assertTrue(placement.y + placement.size <= 306f)
        }
        placements.forEachIndexed { index, first ->
            placements.drop(index + 1).forEach { second ->
                val distance = hypot(
                    first.x + first.size / 2f - second.x - second.size / 2f,
                    first.y + first.size / 2f - second.y - second.size / 2f,
                )
                assertTrue(distance >= first.size / 2f + second.size / 2f)
            }
        }
    }

    @Test
    fun `같은 데이터는 같은 랜덤 배치를 재현한다`() {
        val themes = worryThemeDummyData(
            com.gominitta.android.ui.components.DateRangeOption.LAST_30_DAYS,
        ).rankedThemes()

        assertEquals(
            layoutWorryThemeBubbles(themes),
            layoutWorryThemeBubbles(themes),
        )
    }

    @Test
    fun `비율 데이터가 바뀌면 랜덤 배치도 바뀐다`() {
        val first = worryThemeDummyData(
            com.gominitta.android.ui.components.DateRangeOption.LAST_30_DAYS,
        ).rankedThemes()
        val second = worryThemeDummyData(
            com.gominitta.android.ui.components.DateRangeOption.LAST_2_WEEKS,
        ).rankedThemes()

        assertTrue(
            layoutWorryThemeBubbles(first) != layoutWorryThemeBubbles(second),
        )
    }
}
