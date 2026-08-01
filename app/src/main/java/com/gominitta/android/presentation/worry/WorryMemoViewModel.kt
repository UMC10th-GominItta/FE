package com.gominitta.android.presentation.worry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

/** 한 줄 보태기(B104) — 걱정 예약 시 적은 본문(worryMemo) 바로 아랫줄에 새 내용을 이어붙인다. */
@HiltViewModel
class WorryMemoViewModel @Inject constructor(
    private val repository: SessionRepository,
) : ViewModel() {

    fun addLine(sessionId: Long, line: String, onSaved: () -> Unit) {
        viewModelScope.launch {
            val detail = repository.getSessionDetail(sessionId)
            val updatedMemo = if (detail.worryMemo.isBlank()) line else "${detail.worryMemo}\n$line"
            repository.updateSession(
                sessionId = sessionId,
                worryMemo = updatedMemo,
                scheduledStartAt = detail.scheduledStartAt,
                scheduledEndAt = detail.scheduledEndAt,
            )
            onSaved()
        }
    }
}
