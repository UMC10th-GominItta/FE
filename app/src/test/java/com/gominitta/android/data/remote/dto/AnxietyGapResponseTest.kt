package com.gominitta.android.data.remote.dto

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AnxietyGapResponseTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `명세의 성공 응답을 역직렬화한다`() {
        val response = json.decodeFromString<ApiResponse<AnxietyGapResponse>>(SUCCESS_RESPONSE)

        assertTrue(response.success)
        assertEquals("200", response.code)
        assertEquals("30d", response.data?.period)
        assertEquals(8L, response.data?.beforeScore)
        assertEquals(4L, response.data?.afterScore)
        assertEquals(-4L, response.data?.gap)
        assertEquals(12L, response.data?.sampleCount)
        assertEquals("걱정을 마주하고 마음이 한결 가벼워졌어요.", response.data?.feedback)
    }

    @Test
    fun `명세의 오류 응답을 역직렬화한다`() {
        val response = json.decodeFromString<ApiResponse<AnxietyGapResponse>>(ERROR_RESPONSE)

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
                "beforeScore": 8,
                "afterScore": 4,
                "gap": -4,
                "sampleCount": 12,
                "feedback": "걱정을 마주하고 마음이 한결 가벼워졌어요."
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
