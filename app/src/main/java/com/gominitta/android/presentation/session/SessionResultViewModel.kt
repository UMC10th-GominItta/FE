package com.gominitta.android.presentation.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.model.session.SessionRecord
import com.gominitta.android.domain.usecase.GetSessionDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SessionResultUiState(
    val isLoading: Boolean = true,
    val worryContent: String = "",
    val themeCategory: String? = null,
    val records: List<SessionRecord> = emptyList(),
    val errorMessage: String? = null,
)

/** 완료된 세션 상세 보기 — 마음 세션 목록의 완료 탭에서 카드를 눌러 진입. */
@HiltViewModel
class SessionResultViewModel @Inject constructor(
    private val getSessionDetail: GetSessionDetailUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val sessionId: Long = checkNotNull(savedStateHandle["sessionId"])

    private val _uiState = MutableStateFlow(SessionResultUiState())
    val uiState: StateFlow<SessionResultUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val session = getSessionDetail(sessionId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        worryContent = session.worryContent,
                        themeCategory = session.themeCategory,
                        records = session.records,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
