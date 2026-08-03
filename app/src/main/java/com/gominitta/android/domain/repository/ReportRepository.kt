package com.gominitta.android.domain.repository

import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.model.report.WorryThemeReport

interface ReportRepository {
    suspend fun getWorryThemes(period: String): ApiResult<WorryThemeReport>
}
