package com.gominitta.android.presentation.mypage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.usecase.WithdrawUseCase
import com.gominitta.android.presentation.notification.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WithdrawViewModel @Inject constructor(
    private val withdrawUseCase: WithdrawUseCase,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {
    var isProcessing by mutableStateOf(false); private set
    var isWithdrawn by mutableStateOf(false); private set
    var errorMessage by mutableStateOf<String?>(null); private set

    fun withdraw() {
        if (isProcessing) return
        viewModelScope.launch {
            isProcessing = true
            errorMessage = null
            runCatching { withdrawUseCase() }
                .onSuccess {
                    // 탈퇴는 되돌릴 수 없어, 남은 예약이 울리면 끌 방법이 없다.
                    reminderScheduler.cancelAll()
                    isWithdrawn = true
                }
                .onFailure { errorMessage = it.message ?: "탈퇴에 실패했어요. 잠시 후 다시 시도해 주세요." }
            isProcessing = false
        }
    }
}
