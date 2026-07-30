package com.gominitta.android.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

/** 마음 세션(C103)에서 기록한 텍스트를 확인 화면에서 저장한다. */
@HiltViewModel
class SessionDetailViewModel @Inject constructor(
    private val repository: SessionRepository,
) : ViewModel() {

    fun save(sessionId: Long, recordText: String, onSaved: () -> Unit) {
        viewModelScope.launch {
            repository.addRecord(
                sessionId = sessionId,
                recordType = "text",
                contentText = recordText,
                mediaUrl = null,
            )
            onSaved()
        }
    }
}
