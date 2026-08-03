package com.gominitta.android.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.model.report.AnxietyGapReport
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
 * 걱정 테마 지도와 불안 온도차는 서버에서 조회하고, 아직 명세가 확정되지 않은
 * 걱정 타임라인은 기간이 바뀔 때 더미 데이터를 갱신합니다.
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
            anxietyData = null,
            timelineRange = initialRange,
            timelineData = worryTimelineDummyData(initialRange),
        ),
    )

    // 화면 상태는 내부에서만 변경하고 UI에는 읽기 전용으로 제공합니다.
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    // 카드별 요청을 구분해 기간이 변경된 카드의 요청만 취소합니다.
    private var worryThemeJob: Job? = null
    private var anxietyJob: Job? = null

    // 화면 진입 시 기본 기간인 최근 30일 데이터를 조회합니다.
    init {
        loadWorryThemes(initialRange)
        loadAnxietyReport(initialRange)
    }

    // 선택 기간을 화면에 반영한 뒤 해당 기간의 고민 테마를 다시 조회합니다.
    fun selectWorryThemeRange(range: DateRangeOption) {
        _uiState.update {
            it.copy(
                worryThemeRange = range,
                worryThemeErrorMessage = null,
            )
        }
        loadWorryThemes(range)
    }

    private fun loadWorryThemes(range: DateRangeOption) {
        // 기간을 빠르게 바꿔도 이전 응답이 최신 선택을 덮어쓰지 않도록 기존 요청을 취소합니다.
        worryThemeJob?.cancel()
        worryThemeJob = viewModelScope.launch {
            _uiState.update {
                it.copy(isWorryThemeLoading = true, worryThemeErrorMessage = null)
            }

            // 서버 응답을 고민 테마 데이터 또는 오류 상태로 반영합니다.
            reportRepository.getWorryThemes(range.apiValue).handle(
                onSuccess = { report ->
                    _uiState.update {
                        it.copy(
                            worryThemeData = report.toUiModel(),
                            isWorryThemeLoading = false,
                        )
                    }
                },
                onError = { message ->
                    _uiState.update {
                        it.copy(
                            worryThemeData = null,
                            isWorryThemeLoading = false,
                            worryThemeErrorMessage = message,
                        )
                    }
                },
            )
        }
    }

    // 선택 기간을 화면에 반영한 뒤 해당 기간의 불안 온도차를 다시 조회합니다.
    fun selectAnxietyRange(range: DateRangeOption) {
        _uiState.update {
            it.copy(
                anxietyRange = range,
                anxietyErrorMessage = null,
            )
        }
        loadAnxietyReport(range)
    }

    private fun loadAnxietyReport(range: DateRangeOption) {
        // 각 카드의 요청을 독립적으로 취소해 다른 카드의 로딩 상태에 영향을 주지 않습니다.
        anxietyJob?.cancel()
        anxietyJob = viewModelScope.launch {
            _uiState.update {
                it.copy(isAnxietyLoading = true, anxietyErrorMessage = null)
            }

            // 서버 응답을 불안 온도차 데이터 또는 오류 상태로 반영합니다.
            reportRepository.getAnxietyGap(range.apiValue).handle(
                onSuccess = { report ->
                    _uiState.update {
                        it.copy(
                            anxietyData = report.toUiModel(),
                            isAnxietyLoading = false,
                        )
                    }
                },
                onError = { message ->
                    _uiState.update {
                        it.copy(
                            anxietyData = null,
                            isAnxietyLoading = false,
                            anxietyErrorMessage = message,
                        )
                    }
                },
            )
        }
    }

    // 타임라인 API 확정 전까지 선택 기간에 맞는 더미 데이터를 표시합니다.
    fun selectTimelineRange(range: DateRangeOption) {
        _uiState.update {
            it.copy(
                timelineRange = range,
                timelineData = worryTimelineDummyData(range),
            )
        }
    }
}

private const val NETWORK_ERROR_MESSAGE = "네트워크 연결을 확인해 주세요."

// 공통 API 결과를 성공 데이터와 사용자에게 표시할 오류 메시지로 분기합니다.
private inline fun <T> ApiResult<T>.handle(
    onSuccess: (T) -> Unit,
    onError: (String) -> Unit,
) {
    when (this) {
        is ApiResult.Success -> onSuccess(data)
        is ApiResult.Error -> onError(message)
        is ApiResult.NetworkError -> onError(NETWORK_ERROR_MESSAGE)
    }
}

// 서버의 고민 테마 모델을 화면 표시용 모델로 변환합니다.
private fun WorryThemeReport.toUiModel(): WorryThemeReportData = WorryThemeReportData(
    period = period,
    topCategory = topCategory?.toWorryTheme(),
    themes = themes.mapNotNull { item ->
        item.category.toWorryTheme()?.let { theme ->
            WorryThemeItem(theme = theme, count = item.count)
        }
    },
    feedback = feedback,
)

// 서버의 불안 온도차 모델을 화면 표시용 모델로 변환합니다.
private fun AnxietyGapReport.toUiModel(): AnxietyReportData = AnxietyReportData(
    period = period,
    beforeScore = beforeScore,
    afterScore = afterScore,
    gap = gap,
    sampleCount = sampleCount,
    feedback = feedback,
)

// 서버 카테고리 문자열을 화면에서 사용하는 8개 테마로 변환합니다.
private fun String.toWorryTheme(): WorryTheme? = WorryTheme.entries.firstOrNull { theme ->
    theme.label == this || theme.name.equals(this, ignoreCase = true)
}
