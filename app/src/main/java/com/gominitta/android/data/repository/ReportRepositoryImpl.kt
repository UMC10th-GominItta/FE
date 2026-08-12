package com.gominitta.android.data.repository

import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.data.remote.api.ReportApi
import com.gominitta.android.data.remote.dto.WorryThemeResponse
import com.gominitta.android.data.remote.dto.WorryTimelineResponse
import com.gominitta.android.data.remote.dto.ReportDayOfWeekResponse
import com.gominitta.android.data.remote.dto.ReportTimeSlotResponse
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
            is ApiResult.Success -> ApiResult.Success(result.data.toDomain())
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

private fun WorryTimelineResponse.toDomain(): WorryTimelineReport = WorryTimelineReport(
    period = period,
    cells = cells.map { cell ->
        WorryTimelineCell(
            dayOfWeek = cell.dayOfWeek.toDomain(),
            timeSlot = cell.timeSlot.toDomain(),
            count = cell.count,
        )
    },
    peaks = peaks.map { peak ->
        WorryTimelinePeak(
            dayOfWeek = peak.dayOfWeek.toDomain(),
            timeSlot = peak.timeSlot.toDomain(),
        )
    },
    feedback = feedback,
)

private fun ReportDayOfWeekResponse.toDomain(): ReportDayOfWeek = when (this) {
    ReportDayOfWeekResponse.MON -> ReportDayOfWeek.MON
    ReportDayOfWeekResponse.TUE -> ReportDayOfWeek.TUE
    ReportDayOfWeekResponse.WED -> ReportDayOfWeek.WED
    ReportDayOfWeekResponse.THU -> ReportDayOfWeek.THU
    ReportDayOfWeekResponse.FRI -> ReportDayOfWeek.FRI
    ReportDayOfWeekResponse.SAT -> ReportDayOfWeek.SAT
    ReportDayOfWeekResponse.SUN -> ReportDayOfWeek.SUN
}

private fun ReportTimeSlotResponse.toDomain(): ReportTimeSlot = when (this) {
    ReportTimeSlotResponse.DAWN -> ReportTimeSlot.DAWN
    ReportTimeSlotResponse.MORNING -> ReportTimeSlot.MORNING
    ReportTimeSlotResponse.AFTERNOON -> ReportTimeSlot.AFTERNOON
    ReportTimeSlotResponse.EVENING -> ReportTimeSlot.EVENING
}
