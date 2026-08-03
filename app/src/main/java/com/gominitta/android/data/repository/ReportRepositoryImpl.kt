package com.gominitta.android.data.repository

import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.data.remote.api.ReportApi
import com.gominitta.android.data.remote.dto.WorryThemeResponse
import com.gominitta.android.data.remote.safeApiCall
import com.gominitta.android.domain.model.report.WorryThemeCount
import com.gominitta.android.domain.model.report.WorryThemeReport
import com.gominitta.android.domain.repository.ReportRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepositoryImpl @Inject constructor(
    private val reportApi: ReportApi,
) : ReportRepository {
    override suspend fun getWorryThemes(period: String): ApiResult<WorryThemeReport> =
        when (val result = safeApiCall { reportApi.getWorryThemes(period) }) {
            is ApiResult.Success -> ApiResult.Success(result.data.toDomain())
            is ApiResult.Error -> result
            is ApiResult.NetworkError -> result
        }
}

private fun WorryThemeResponse.toDomain(): WorryThemeReport = WorryThemeReport(
    period = period,
    topCategory = topCategory,
    themes = themes.map { WorryThemeCount(category = it.category, count = it.count) },
    feedback = feedback,
)
