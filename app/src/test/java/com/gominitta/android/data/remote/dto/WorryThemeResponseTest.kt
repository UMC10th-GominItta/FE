package com.gominitta.android.data.remote.dto

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WorryThemeResponseTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `명세의 성공 응답을 역직렬화한다`() {
        val response = json.decodeFromString<ApiResponse<WorryThemeResponse>>(SUCCESS_RESPONSE)

        assertTrue(response.success)
        assertEquals("200", response.code)
        assertEquals("30d", response.data?.period)
        assertEquals("진로", response.data?.topCategory)
        assertEquals(8, response.data?.themes?.size)
        assertEquals(WorryThemeCountResponse("진로", 23), response.data?.themes?.first())
        assertEquals(WorryThemeCountResponse("발표", 4), response.data?.themes?.last())
        assertEquals("최근에는 진로와 관련된 걱정이 가장 많았어요.", response.data?.feedback)
    }

    @Test
    fun `명세의 오류 응답을 역직렬화한다`() {
        val response = json.decodeFromString<ApiResponse<WorryThemeResponse>>(ERROR_RESPONSE)

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
                "topCategory": "진로",
                "themes": [
                  { "category": "진로", "count": 23 },
                  { "category": "관계", "count": 18 },
                  { "category": "학업", "count": 16 },
                  { "category": "건강", "count": 11 },
                  { "category": "가족", "count": 9 },
                  { "category": "취업", "count": 8 },
                  { "category": "돈", "count": 7 },
                  { "category": "발표", "count": 4 }
                ],
                "feedback": "최근에는 진로와 관련된 걱정이 가장 많았어요."
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
