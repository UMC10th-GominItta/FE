package com.gominitta.android.data.repository

import com.gominitta.android.data.remote.dto.WorryTimelineCellResponse
import com.gominitta.android.domain.model.report.ReportDayOfWeek
import com.gominitta.android.domain.model.report.ReportTimeSlot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ReportRepositoryImplTest {
    @Test
    fun `모든 요일 응답 형식을 누락 없이 변환한다`() {
        val dayCases = listOf(
            Triple("MON", "MONDAY", ReportDayOfWeek.MON),
            Triple("TUE", "TUESDAY", ReportDayOfWeek.TUE),
            Triple("WED", "WEDNESDAY", ReportDayOfWeek.WED),
            Triple("THU", "THURSDAY", ReportDayOfWeek.THU),
            Triple("FRI", "FRIDAY", ReportDayOfWeek.FRI),
            Triple("SAT", "SATURDAY", ReportDayOfWeek.SAT),
            Triple("SUN", "SUNDAY", ReportDayOfWeek.SUN),
        )

        dayCases.forEach { (shortName, fullName, expected) ->
            listOf(shortName, fullName, " ${fullName.lowercase()} ").forEach { value ->
                val cell = WorryTimelineCellResponse(value, "MORNING", 1).toDomain()
                assertEquals("dayOfWeek=$value", expected, cell?.dayOfWeek)
            }
        }
    }

    @Test
    fun `모든 시간대 응답 형식을 누락 없이 변환한다`() {
        val timeSlotCases = listOf(
            Triple("밤", "DAWN", ReportTimeSlot.DAWN),
            Triple("아침", "MORNING", ReportTimeSlot.MORNING),
            Triple("오후", "AFTERNOON", ReportTimeSlot.AFTERNOON),
            Triple("저녁", "EVENING", ReportTimeSlot.EVENING),
        )

        timeSlotCases.forEach { (koreanName, enumName, expected) ->
            listOf(koreanName, enumName, " ${enumName.lowercase()} ").forEach { value ->
                val cell = WorryTimelineCellResponse("MONDAY", value, 1).toDomain()
                assertEquals("timeSlot=$value", expected, cell?.timeSlot)
            }
        }
    }

    @Test
    fun `목요일 22시 enum 응답을 저녁 셀로 변환한다`() {
        val cell = WorryTimelineCellResponse(
            dayOfWeek = "THU",
            timeSlot = "EVENING",
            count = 1,
        ).toDomain()

        assertNotNull(cell)
        assertEquals(ReportDayOfWeek.THU, cell?.dayOfWeek)
        assertEquals(ReportTimeSlot.EVENING, cell?.timeSlot)
        assertEquals(1L, cell?.count)
    }

    @Test
    fun `기존 한글 시간대와 전체 요일 응답도 계속 변환한다`() {
        val cell = WorryTimelineCellResponse(
            dayOfWeek = "THURSDAY",
            timeSlot = "저녁",
            count = 2,
        ).toDomain()

        assertEquals(ReportDayOfWeek.THU, cell?.dayOfWeek)
        assertEquals(ReportTimeSlot.EVENING, cell?.timeSlot)
    }
}
