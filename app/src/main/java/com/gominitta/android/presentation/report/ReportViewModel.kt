package com.gominitta.android.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.model.report.WorryThemeReport
import com.gominitta.android.domain.repository.ReportRepository
import com.gominitta.android.ui.components.DateRangeOption
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 마음 리포트의 기간 선택과 카드별 데이터를 관리합니다.
 *
 * 걱정 테마 지도는 서버에서 조회하고, 아직 명세가 확정되지 않은 나머지 두 카드는
 * 기간이 바뀔 때 더미 데이터를 갱신합니다.
 */
@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository,
) : ViewModel() {

    private val initialRange = DateRangeOption.LAST_30_DAYS
    private val _uiState = MutableStateFlow(
        ReportUiState(
            worryThemeRange = initialRange,
            worryThemeData = null,
            anxietyRange = initialRange,
            anxietyData = anxietyDummyData(initialRange),
            timelineRange = initialRange,
            timelineData = worryTimelineDummyData(initialRange),
        ),
    )
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()
    private var worryThemeJob: Job? = null

    init {
        loadWorryThemes(initialRange)
    }

    fun selectWorryThemeRange(range: DateRangeOption) {
        _uiState.update {
            it.copy(
                worryThemeRange = range,
                errorMessage = null,
            )
        }
        loadWorryThemes(range)
    }

    private fun loadWorryThemes(range: DateRangeOption) {
        worryThemeJob?.cancel()
        worryThemeJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = reportRepository.getWorryThemes(range.apiValue)) {
                is ApiResult.Success -> _uiState.update {
                    it.copy(
                        worryThemeData = result.data.toUiModel(),
                        isLoading = false,
                    )
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(
                        worryThemeData = null,
                        isLoading = false,
                        errorMessage = result.message,
                    )
                }
                is ApiResult.NetworkError -> _uiState.update {
                    it.copy(
                        worryThemeData = null,
                        isLoading = false,
                        errorMessage = "네트워크 연결을 확인해 주세요.",
                    )
                }
            }
        }
    }

    fun selectAnxietyRange(range: DateRangeOption) {
        _uiState.update {
            it.copy(
                anxietyRange = range,
                anxietyData = anxietyDummyData(range),
                errorMessage = null,
            )
        }
    }

    fun selectTimelineRange(range: DateRangeOption) {
        _uiState.update {
            it.copy(
                timelineRange = range,
                timelineData = worryTimelineDummyData(range),
                errorMessage = null,
            )
        }
    }
}

private fun WorryThemeReport.toUiModel(): WorryThemeReportData = WorryThemeReportData(
    period = period,
    topCategory = topCategory?.toWorryTheme(),
    themes = themes.mapNotNull { item ->
        item.category.toWorryTheme()?.let { theme ->
            WorryThemeItem(
                theme = theme,
                count = item.count,
            )
        }
    },
    feedback = feedback,
)

private fun String.toWorryTheme(): WorryTheme? = WorryTheme.entries.firstOrNull { theme ->
    theme.label == this || theme.name.equals(this, ignoreCase = true)
}
