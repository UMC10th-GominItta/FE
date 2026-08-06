package com.gominitta.android.presentation.report

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.hypot

class WorryThemeReportModelTest {
    @Test
    fun `3건 미만이면 차트를 렌더링하지 않는다`() {
        assertFalse(report(WorryThemeItem(WorryTheme.CAREER, 2)).canRender)
        assertTrue(report(WorryThemeItem(WorryTheme.CAREER, 3)).canRender)
    }

    @Test
    fun `모든 비율이 30퍼센트 미만이면 첫 최상위 테마만 승격한다`() {
        val data = report(
                WorryThemeItem(WorryTheme.CAREER, 25),
                WorryThemeItem(WorryTheme.STUDY, 25),
                WorryThemeItem(WorryTheme.HEALTH, 20),
                WorryThemeItem(WorryTheme.MONEY, 15),
                WorryThemeItem(WorryTheme.FAMILY, 10),
                WorryThemeItem(WorryTheme.PRESENTATION, 5),
        )

        val ranked = data.rankedThemes()

        assertEquals(6, ranked.size)
        assertEquals(WorryThemeWeight.PRIMARY, ranked[0].weight)
        assertEquals(WorryThemeWeight.NORMAL, ranked[1].weight)
    }

    @Test
    fun `서버 피드백을 그대로 보관한다`() {
        val data = report(feedback = "서버가 만든 피드백")

        assertEquals("서버가 만든 피드백", data.feedback)
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

    private fun report(
        vararg themes: WorryThemeItem,
        feedback: String = "",
    ) = WorryThemeReportData(
        period = "30d",
        topCategory = themes.maxByOrNull { it.count }?.theme,
        themes = themes.toList(),
        feedback = feedback,
    )
}
