package com.gominitta.android.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.usecase.GetMyProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginCompleteViewModel @Inject constructor(
    private val getMyProfile: GetMyProfileUseCase,
) : ViewModel() {

    private val _nickname = MutableStateFlow("")
    val nickname = _nickname.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching { getMyProfile() }.getOrNull()?.let { _nickname.value = it.nickname }
        }
    }
}
