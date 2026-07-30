package com.gominitta.android.presentation.report

import androidx.lifecycle.ViewModel
import com.gominitta.android.ui.components.DateRangeOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * 마음 리포트의 기간 선택과 카드별 데이터를 관리합니다.
 *
 * 현재는 API 연결 전이므로 기간이 바뀔 때 더미 데이터를 갱신합니다. 실제 API 연결 시
 * 각 더미 함수 호출을 Repository 호출로 교체하고 로딩·오류 상태를 함께 갱신하면 됩니다.
 */
class ReportViewModel : ViewModel() {

    private val initialRange = DateRangeOption.LAST_30_DAYS
    private val _uiState = MutableStateFlow(
        ReportUiState(
            worryThemeRange = initialRange,
            worryThemeData = worryThemeDummyData(initialRange),
            anxietyRange = initialRange,
            anxietyData = anxietyDummyData(initialRange),
            timelineRange = initialRange,
            timelineData = worryTimelineDummyData(initialRange),
        ),
    )
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    fun selectWorryThemeRange(range: DateRangeOption) {
        _uiState.update {
            it.copy(
                worryThemeRange = range,
                worryThemeData = worryThemeDummyData(range),
                errorMessage = null,
            )
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
