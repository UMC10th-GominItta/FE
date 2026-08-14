package com.gominitta.android.presentation.report

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorryTimelineReportModelTest {
    @Test
    fun `걱정 데이터가 5건 이상일 때만 렌더링한다`() {
        assertFalse(WorryTimelineReportData(4, emptyList(), "").canRender)
        assertTrue(WorryTimelineReportData(5, emptyList(), "").canRender)
    }

    @Test
    fun `서버가 충분하다고 응답해도 5건 미만이면 렌더링하지 않는다`() {
        assertFalse(WorryTimelineReportData(10, emptyList(), "", false).canRender)
        assertFalse(WorryTimelineReportData(0, emptyList(), "", true).canRender)
        assertTrue(WorryTimelineReportData(5, emptyList(), "", true).canRender)
    }

    @Test
    fun `서버 피드백을 그대로 보관한다`() {
        val data = WorryTimelineReportData(
            totalCount = 5,
            levels = emptyList(),
            feedback = "서버가 만든 피드백",
        )

        assertEquals("서버가 만든 피드백", data.feedback)
    }
}
