package com.gominitta.android.presentation.worry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.usecase.CreateWorryUseCase
import com.gominitta.android.domain.usecase.GetFavoriteTimesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorryReservationUiState())
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
                is ApiResult.Success -> _uiState.update { it.copy(saveState = WorrySaveState.Success(result.data)) }
                is ApiResult.Error -> _uiState.update { it.copy(saveState = WorrySaveState.Error(result.message)) }
                is ApiResult.NetworkError ->
                    _uiState.update { it.copy(saveState = WorrySaveState.Error("네트워크 연결을 확인해주세요.")) }
            }
        }
    }
}
