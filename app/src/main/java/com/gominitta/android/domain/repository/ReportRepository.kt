package com.gominitta.android.domain.repository

import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.model.report.WorryThemeReport
import com.gominitta.android.domain.model.report.AnxietyGapReport
import com.gominitta.android.domain.model.report.WorryTimelineReport

interface ReportRepository {
    suspend fun getWorryThemes(period: String): ApiResult<WorryThemeReport>
    suspend fun getAnxietyGap(period: String): ApiResult<AnxietyGapReport>
    suspend fun getWorryTimeline(period: String): ApiResult<WorryTimelineReport>
}
