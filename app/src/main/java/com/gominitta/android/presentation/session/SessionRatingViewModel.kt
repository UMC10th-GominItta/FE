package com.gominitta.android.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

/** 마음 세션 평가(C104-2) 저장 — 세션을 실제로 완료 처리한다. */
@HiltViewModel
class SessionRatingViewModel @Inject constructor(
    private val repository: SessionRepository,
) : ViewModel() {

    fun save(sessionId: Long, emotionScoreAfter: Int, onSaved: () -> Unit) {
        viewModelScope.launch {
            repository.completeSession(sessionId, emotionScoreAfter)
            onSaved()
        }
    }
}
