package com.gominitta.android.presentation.report

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorryTimelineReportModelTest {
    @Test
    fun `걱정 데이터가 5건 이상일 때만 렌더링한다`() {
        assertFalse(WorryTimelineReportData(4, emptyList()).canRender)
        assertTrue(WorryTimelineReportData(5, emptyList()).canRender)
    }

    @Test
    fun `Level 4 셀을 최대 두 개까지 피드백에 표시한다`() {
        val data = WorryTimelineReportData(
            totalCount = 5,
            levels = listOf(
                listOf(0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 0),
                listOf(0, 0, 0, 4, 0, 0, 0),
                listOf(0, 0, 0, 0, 0, 0, 4),
            ),
        )

        assertEquals(
            "목요일 저녁 시간대(18-24시)와 일요일 밤 시간대(00-06시)에 걱정 기록이 많았어요.",
            data.feedbackText(),
        )
    }

    @Test
    fun `Level 4 셀이 하나이면 한 시간대만 표시한다`() {
        val data = WorryTimelineReportData(
            totalCount = 5,
            levels = listOf(listOf(4, 0, 0, 0, 0, 0, 0)),
        )
        assertEquals(
            "월요일 아침 시간대(06-12시)에 걱정 기록이 많았어요.",
            data.feedbackText(),
        )
    }
}
