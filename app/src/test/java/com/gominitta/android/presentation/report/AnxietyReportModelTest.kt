package com.gominitta.android.presentation.report

import com.gominitta.android.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnxietyReportModelTest {
    @Test
    fun `표본이 0개일 때만 빈 상태를 표시한다`() {
        assertFalse(report(sampleCount = 0).canRender)
        assertTrue(report(sampleCount = 1).canRender)
    }

    @Test
    fun `세션 후 점수가 낮으면 감소 상태와 배지를 만든다`() {
        val data = report(beforeScore = 8.0, afterScore = 4.0, gap = -4.0)
        assertEquals(AnxietyChangeState.DECREASED, data.state)
        assertEquals("- 4점 감소", data.badgeText)
    }

    @Test
    fun `동일 점수는 유지이고 높은 점수는 상승이다`() {
        assertEquals(
            AnxietyChangeState.MAINTAINED,
            report(beforeScore = 6.0, afterScore = 6.0, gap = 0.0).state,
        )
        assertEquals(
            "+ 1.5점 상승",
            report(beforeScore = 5.0, afterScore = 6.5, gap = 1.5).badgeText,
        )
    }

    @Test
    fun `평균 점수를 가장 가까운 구간의 고양이 일러스트로 매핑한다`() {
        assertEquals(R.drawable.worry_cat_0, anxietyScoreIllustration(0.0))
        assertEquals(R.drawable.worry_cat_1_2, anxietyScoreIllustration(1.6))
        assertEquals(R.drawable.worry_cat_3_4, anxietyScoreIllustration(3.4))
        assertEquals(R.drawable.worry_cat_5_6, anxietyScoreIllustration(5.5))
        assertEquals(R.drawable.worry_cat_7_8, anxietyScoreIllustration(7.8))
        assertEquals(R.drawable.worry_cat_9_10, anxietyScoreIllustration(9.2))
        assertEquals(R.drawable.worry_cat_0, anxietyScoreIllustration(-1.0))
        assertEquals(R.drawable.worry_cat_9_10, anxietyScoreIllustration(11.0))
    }

    private fun report(
        beforeScore: Double = 8.0,
        afterScore: Double = 4.0,
        gap: Double = afterScore - beforeScore,
        sampleCount: Long = 2,
    ) = AnxietyReportData(
        period = "30d",
        beforeScore = beforeScore,
        afterScore = afterScore,
        gap = gap,
        sampleCount = sampleCount,
        feedback = "걱정을 마주하고 마음이 한결 가벼워졌어요.",
    )
}
