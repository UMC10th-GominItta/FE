package com.gominitta.android.presentation.worry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.usecase.CreateWorryUseCase
import com.gominitta.android.domain.usecase.GetFavoriteTimesUseCase
import com.gominitta.android.presentation.notification.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** 걱정 예약 저장(POST /api/v1/worries) 진행 상태. */
sealed interface WorrySaveState {
    data object Idle : WorrySaveState
    data object Loading : WorrySaveState
    data class Success(val worryId: Long) : WorrySaveState
    data class Error(val message: String) : WorrySaveState
}

/**
 * 걱정 예약 플로우(WorryInput → WorryIntensity → WorrySchedule → WorrySaved) 공유 ViewModel.
 * [com.gominitta.android.navigation.AppNavHost]에서 WORRY_INPUT 백스택 엔트리에 스코프되어
 * 4개 화면이 같은 인스턴스를 공유한다.
 */
@HiltViewModel
class WorryReservationViewModel @Inject constructor(
    private val createWorry: CreateWorryUseCase,
    private val getFavoriteTimes: GetFavoriteTimesUseCase,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {

    private val _uiState = MutableStateFlow(defaultReservationState())
    val uiState: StateFlow<WorryReservationUiState> = _uiState.asStateFlow()

    init {
        loadFavoriteTimes()
    }

    private fun loadFavoriteTimes() {
        viewModelScope.launch {
            val times = runCatching { getFavoriteTimes() }.getOrDefault(emptyList())
            _uiState.update { it.copy(favoriteTimes = times) }
        }
    }

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun onContentChange(content: String) {
        _uiState.update { it.copy(content = content) }
    }

    fun onIntensityChange(intensity: Int) {
        _uiState.update { it.copy(intensity = intensity) }
    }

    fun onScheduleChange(startTime: LocalDateTime?, endTime: LocalDateTime?) {
        _uiState.update { it.copy(startTime = startTime, endTime = endTime) }
    }

    fun save() {
        val state = _uiState.value
        val start = state.startTime ?: return
        val end = state.endTime ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(saveState = WorrySaveState.Loading) }
            when (
                val result = createWorry(
                    title = state.title,
                    content = state.content,
                    emotionScoreBefore = state.intensity,
                    scheduledStartAt = start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    scheduledEndAt = end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                )
            ) {
                is ApiResult.Success -> {
                    reminderScheduler.scheduleWorryReminder(result.data, start)
                    reminderScheduler.scheduleSessionStartAlarm(result.data, start)
                    _uiState.update { it.copy(saveState = WorrySaveState.Success(result.data)) }
                }
                is ApiResult.Error -> _uiState.update { it.copy(saveState = WorrySaveState.Error(result.message)) }
                is ApiResult.NetworkError ->
                    _uiState.update { it.copy(saveState = WorrySaveState.Error("네트워크 연결을 확인해주세요.")) }
            }
        }
    }
}

/**
 * 예약 시간 기본값 = 오늘 9~10PM. 단, 기본 시작 시각이 이미 과거면 선택을 비워두어
 * (startTime/endTime = null) 사용자가 피커로 유효한 시각을 고르게 한다. 과거가 아니면
 * 기본값이 그대로 선택된 상태라 '다음'이 바로 활성화된다.
 */
private fun defaultReservationState(): WorryReservationUiState {
    val start = LocalDate.now().atTime(21, 0)
    val end = LocalDate.now().atTime(22, 0)
    return if (start.isBefore(LocalDateTime.now())) {
        WorryReservationUiState()
    } else {
        WorryReservationUiState(startTime = start, endTime = end)
    }
}
