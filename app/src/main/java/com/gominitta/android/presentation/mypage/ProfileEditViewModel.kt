package com.gominitta.android.presentation.mypage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.gominitta.android.presentation.mypage.model.DummyMyPageRepository
import com.gominitta.android.presentation.mypage.model.MyPageRepository

class ProfileEditViewModel @JvmOverloads constructor(
    private val repository: MyPageRepository = DummyMyPageRepository,
) : ViewModel() {

    var nickname by mutableStateOf("")
        private set

    var selectedProfileIndex by mutableIntStateOf(repository.getProfileImageIndex())
        private set

    val initialNickname: String get() = repository.getNickname()

    fun onNicknameChange(value: String) {
        nickname = value.take(12)
    }

    fun onProfileSelected(index: Int) {
        selectedProfileIndex = index
    }

    fun save() {
        repository.setProfileImageIndex(selectedProfileIndex)
        // TODO: 닉네임 저장은 API 연동 시 UseCase 호출로 대체
    }
}