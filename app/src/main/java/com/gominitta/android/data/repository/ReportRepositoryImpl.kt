package com.gominitta.android.data.repository

import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.data.remote.api.ReportApi
import com.gominitta.android.data.remote.dto.WorryThemeResponse
import com.gominitta.android.data.remote.dto.WorryTimelineResponse
import com.gominitta.android.data.remote.dto.AnxietyGapResponse
import com.gominitta.android.data.remote.safeApiCall
import com.gominitta.android.domain.model.report.WorryThemeCount
import com.gominitta.android.domain.model.report.WorryThemeReport
import com.gominitta.android.domain.model.report.AnxietyGapReport
import com.gominitta.android.domain.model.report.ReportDayOfWeek
import com.gominitta.android.domain.model.report.ReportTimeSlot
import com.gominitta.android.domain.model.report.WorryTimelineCell
import com.gominitta.android.domain.model.report.WorryTimelinePeak
import com.gominitta.android.domain.model.report.WorryTimelineReport
import com.gominitta.android.domain.repository.ReportRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepositoryImpl @Inject constructor(
    private val reportApi: ReportApi,
) : ReportRepository {
    override suspend fun getWorryThemes(period: String): ApiResult<WorryThemeReport> =
        when (val result = safeApiCall { reportApi.getWorryThemes(period) }) {
            is ApiResult.Success -> ApiResult.Success(result.data.toDomain(period))
            is ApiResult.Error -> result
            is ApiResult.NetworkError -> result
        }

    override suspend fun getAnxietyGap(period: String): ApiResult<AnxietyGapReport> =
        when (val result = safeApiCall { reportApi.getAnxietyGap(period) }) {
            is ApiResult.Success -> ApiResult.Success(result.data.toDomain(period))
            is ApiResult.Error -> result
            is ApiResult.NetworkError -> result
        }

    override suspend fun getWorryTimeline(period: String): ApiResult<WorryTimelineReport> =
        when (val result = safeApiCall { reportApi.getWorryTimeline(period) }) {
            is ApiResult.Success -> ApiResult.Success(result.data.toDomain(period))
            is ApiResult.Error -> result
            is ApiResult.NetworkError -> result
        }
}

private fun WorryThemeResponse.toDomain(period: String): WorryThemeReport = WorryThemeReport(
    period = period,
    hasEnoughData = hasEnoughData,
    topTheme = topTheme,
    totalCount = totalCount,
    themes = themes.map { WorryThemeCount(theme = it.theme, count = it.count) },
)

private fun AnxietyGapResponse.toDomain(period: String): AnxietyGapReport = AnxietyGapReport(
    period = period,
    hasEnoughData = hasEnoughData,
    avgBefore = avgBefore,
    avgAfter = avgAfter,
    gap = gap,
    improved = improved,
)

private fun WorryTimelineResponse.toDomain(period: String): WorryTimelineReport = WorryTimelineReport(
    period = period,
    hasEnoughData = hasEnoughData,
    cells = cells.mapNotNull { it.toDomain() },
    topCells = topCells.mapNotNull { it.toDomain() },
)

internal fun com.gominitta.android.data.remote.dto.WorryTimelineCellResponse.toDomain(): WorryTimelineCell? {
    val day = when (dayOfWeek.trim().uppercase()) {
        "MON", "MONDAY" -> ReportDayOfWeek.MON
        "TUE", "TUESDAY" -> ReportDayOfWeek.TUE
        "WED", "WEDNESDAY" -> ReportDayOfWeek.WED
        "THU", "THURSDAY" -> ReportDayOfWeek.THU
        "FRI", "FRIDAY" -> ReportDayOfWeek.FRI
        "SAT", "SATURDAY" -> ReportDayOfWeek.SAT
        "SUN", "SUNDAY" -> ReportDayOfWeek.SUN
        else -> null
    }
    val slot = when (timeSlot.trim().uppercase()) {
        "밤", "DAWN" -> ReportTimeSlot.DAWN
        "아침", "MORNING" -> ReportTimeSlot.MORNING
        "오후", "AFTERNOON" -> ReportTimeSlot.AFTERNOON
        "저녁", "EVENING" -> ReportTimeSlot.EVENING
        else -> null
    }
    return if (day != null && slot != null) WorryTimelineCell(day, slot, count) else null
}
