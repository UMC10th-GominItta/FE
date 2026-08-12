package com.gominitta.android.presentation.mypage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.usecase.WithdrawUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WithdrawViewModel @Inject constructor(
    private val withdrawUseCase: WithdrawUseCase,
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
                .onSuccess { isWithdrawn = true }
                .onFailure { errorMessage = it.message ?: "탈퇴에 실패했어요. 잠시 후 다시 시도해 주세요." }
            isProcessing = false
        }
    }
}
