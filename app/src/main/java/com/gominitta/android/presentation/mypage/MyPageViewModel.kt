package com.gominitta.android.presentation.mypage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.usecase.GetUserProfileUseCase
import com.gominitta.android.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    var nickname by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var profileImageUrl by mutableStateOf("")
        private set

    var isLoggedOut by mutableStateOf(false)
        private set

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val user = getUserProfileUseCase()
            nickname = user.nickname
            email = user.email
            profileImageUrl = user.profileImageUrl
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            isLoggedOut = true
        }
    }
}