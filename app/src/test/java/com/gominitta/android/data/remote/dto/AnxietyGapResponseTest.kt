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
        assertTrue(response.data?.hasEnoughData == true)
        assertEquals(8, response.data?.avgBefore)
        assertEquals(4, response.data?.avgAfter)
        assertEquals(-4, response.data?.gap)
        assertTrue(response.data?.improved == true)
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
                "hasEnoughData": true,
                "avgBefore": 8,
                "avgAfter": 4,
                "gap": -4,
                "improved": true
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
