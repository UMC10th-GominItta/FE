package com.gominitta.android.presentation.session

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/** 예약된 걱정 수정(C105) — [sessionId]의 실제 메모/시간을 불러오고, 저장·삭제를 반영한다. */
@HiltViewModel
class SessionEditViewModel @Inject constructor(
    private val repository: SessionRepository,
) : ViewModel() {

    var worryTitle by mutableStateOf("")
        private set
    var initialWorryMemo by mutableStateOf("")
        private set
    var initialStartAt by mutableStateOf<LocalDateTime?>(null)
        private set
    var initialEndAt by mutableStateOf<LocalDateTime?>(null)
        private set

    private var loadedSessionId: Long? = null

    fun load(sessionId: Long) {
        if (loadedSessionId == sessionId) return
        loadedSessionId = sessionId
        viewModelScope.launch {
            try {
                val detail = repository.getSessionDetail(sessionId)
                worryTitle = detail.worryContent
                initialWorryMemo = detail.worryMemo
                initialStartAt = detail.scheduledStartAt
                initialEndAt = detail.scheduledEndAt
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                loadedSessionId = null
                e.printStackTrace()
            }
        }
    }

    fun save(
        sessionId: Long,
        worryMemo: String,
        scheduledStartAt: LocalDateTime,
        scheduledEndAt: LocalDateTime,
        onSaved: () -> Unit,
    ) {
        viewModelScope.launch {
            repository.updateSession(sessionId, worryMemo, scheduledStartAt, scheduledEndAt)
            onSaved()
        }
    }

    fun delete(sessionId: Long, onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
            onDeleted()
        }
    }
}
