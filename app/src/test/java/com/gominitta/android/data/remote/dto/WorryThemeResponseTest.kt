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
        assertTrue(response.data?.hasEnoughData == true)
        assertEquals("진로", response.data?.topTheme)
        assertEquals(96L, response.data?.totalCount)
        assertEquals(8, response.data?.themes?.size)
        assertEquals(WorryThemeCountResponse("진로", 23), response.data?.themes?.first())
        assertEquals(WorryThemeCountResponse("기타", 4), response.data?.themes?.last())
    }

    @Test
    fun `명세의 오류 응답을 역직렬화한다`() {
        val response = json.decodeFromString<ApiResponse<WorryThemeResponse>>(ERROR_RESPONSE)

        assertFalse(response.success)
        assertEquals("REPORT_400", response.code)
        assertEquals("지원하지 않는 조회 기간입니다.", response.message)
        assertNull(response.data)
    }

    @Test
    fun `데이터 부족 응답의 null 최상위 테마를 역직렬화한다`() {
        val response = json.decodeFromString<ApiResponse<WorryThemeResponse>>(NOT_ENOUGH_DATA_RESPONSE)

        assertTrue(response.success)
        assertFalse(response.data?.hasEnoughData ?: true)
        assertNull(response.data?.topTheme)
        assertEquals(0L, response.data?.totalCount)
        assertTrue(response.data?.themes?.isEmpty() == true)
    }

    private companion object {
        val SUCCESS_RESPONSE = """
            {
              "success": true,
              "code": "200",
              "message": "요청이 성공했습니다.",
              "data": {
                "hasEnoughData": true,
                "topTheme": "진로",
                "totalCount": 96,
                "themes": [
                  { "theme": "진로", "count": 23 },
                  { "theme": "관계", "count": 18 },
                  { "theme": "학업", "count": 16 },
                  { "theme": "건강", "count": 11 },
                  { "theme": "가족", "count": 9 },
                  { "theme": "취업", "count": 8 },
                  { "theme": "돈", "count": 7 },
                  { "theme": "기타", "count": 4 }
                ]
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

        val NOT_ENOUGH_DATA_RESPONSE = """
            {
              "success": true,
              "code": "200",
              "message": "요청이 성공했습니다.",
              "data": {
                "hasEnoughData": false,
                "topTheme": null,
                "totalCount": 0,
                "themes": []
              }
            }
        """.trimIndent()
    }
}
