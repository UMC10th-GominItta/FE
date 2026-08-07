package com.gominitta.android.data.remote.dto

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WorryTimelineResponseTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `명세의 성공 응답을 역직렬화한다`() {
        val response = json.decodeFromString<ApiResponse<WorryTimelineResponse>>(SUCCESS_RESPONSE)

        assertTrue(response.success)
        assertEquals("30d", response.data?.period)
        assertEquals(3, response.data?.cells?.size)
        assertEquals(6L, response.data?.cells?.get(1)?.count)
        assertEquals(ReportDayOfWeekResponse.THU, response.data?.peaks?.first()?.dayOfWeek)
        assertEquals(ReportTimeSlotResponse.EVENING, response.data?.peaks?.first()?.timeSlot)
    }

    @Test
    fun `명세의 오류 응답을 역직렬화한다`() {
        val response = json.decodeFromString<ApiResponse<WorryTimelineResponse>>(ERROR_RESPONSE)

        assertFalse(response.success)
        assertEquals("REPORT_400", response.code)
        assertEquals("지원하지 않는 조회 기간입니다.", response.message)
        assertNull(response.data)
    }

    private companion object {
        val SUCCESS_RESPONSE = """
            {
              "success": true,
              "code": "200",
              "message": "요청이 성공했습니다.",
              "data": {
                "period": "30d",
                "cells": [
                  { "dayOfWeek": "MON", "timeSlot": "MORNING", "count": 1 },
                  { "dayOfWeek": "THU", "timeSlot": "EVENING", "count": 6 },
                  { "dayOfWeek": "SUN", "timeSlot": "DAWN", "count": 5 }
                ],
                "peaks": [
                  { "dayOfWeek": "THU", "timeSlot": "EVENING" },
                  { "dayOfWeek": "SUN", "timeSlot": "DAWN" }
                ],
                "feedback": "목요일 저녁 시간대와 일요일 밤 시간대에 걱정 기록이 많았어요."
              }
            }
        """.trimIndent()

        val ERROR_RESPONSE = """
            {
              "success": false,
              "code": "REPORT_400",
              "message": "지원하지 않는 조회 기간입니다.",
              "data": null
            }
        """.trimIndent()
    }
}
