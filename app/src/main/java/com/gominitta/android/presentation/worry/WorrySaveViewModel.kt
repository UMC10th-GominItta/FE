package com.gominitta.android.presentation.worry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.usecase.CreateSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.launch

/** 걱정 예약 플로우 마지막 단계(WORRY_SCHEDULE → WORRY_SAVED)에서 실제로 세션을 생성한다. */
@HiltViewModel
class WorrySaveViewModel @Inject constructor(
    private val createSession: CreateSessionUseCase,
) : ViewModel() {

    fun save(
        worryContent: String,
        worryMemo: String,
        scheduledStartAt: LocalDateTime,
        scheduledEndAt: LocalDateTime,
        emotionScoreBefore: Int?,
        onSaved: () -> Unit,
    ) {
        viewModelScope.launch {
            createSession(worryContent, worryMemo, scheduledStartAt, scheduledEndAt, emotionScoreBefore)
            onSaved()
        }
    }
}
