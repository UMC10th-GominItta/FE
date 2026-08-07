package com.gominitta.android.presentation.mypage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.usecase.GetUserProfileUseCase
import com.gominitta.android.domain.usecase.UpdateUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
) : ViewModel() {

    var nickname by mutableStateOf("")
        private set

    var initialNickname by mutableStateOf("")
        private set

    var selectedProfileIndex by mutableIntStateOf(0)
        private set

    var isSaved by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            val user = getUserProfileUseCase()
            initialNickname = user.nickname
            selectedProfileIndex = user.profileImageUrl.toProfileIndex()
        }
    }

    fun onNicknameChange(value: String) {
        nickname = value.take(12)
    }

    fun onProfileSelected(index: Int) {
        selectedProfileIndex = index
    }

    fun save() {
        viewModelScope.launch {
            updateUserProfileUseCase(
                nickname = nickname.ifBlank { null },
                profileImageUrl = selectedProfileIndex.toProfileImageUrl(),
            )
            isSaved = true
        }
    }
}