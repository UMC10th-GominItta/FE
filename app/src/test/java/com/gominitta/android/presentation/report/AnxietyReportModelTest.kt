package com.gominitta.android.presentation.report

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnxietyReportModelTest {
    @Test
    fun `유효 매칭이 2세트 이상일 때만 렌더링한다`() {
        assertFalse(AnxietyReportData(1, 8.0, 4.0).canRender)
        assertTrue(AnxietyReportData(2, 8.0, 4.0).canRender)
    }

    @Test
    fun `세션 후 점수가 낮으면 감소 상태와 배지를 만든다`() {
        val data = AnxietyReportData(2, 8.0, 4.0)
        assertEquals(AnxietyChangeState.DECREASED, data.state)
        assertEquals("- 4점 감소", data.badgeText)
    }

    @Test
    fun `동일 점수는 유지이고 높은 점수는 상승이다`() {
        assertEquals(
            AnxietyChangeState.MAINTAINED,
            AnxietyReportData(2, 6.0, 6.0).state,
        )
        assertEquals(
            "+ 1.5점 상승",
            AnxietyReportData(2, 5.0, 6.5).badgeText,
        )
    }
}
