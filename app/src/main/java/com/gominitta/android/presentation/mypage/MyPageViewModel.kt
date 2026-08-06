package com.gominitta.android.presentation.mypage

import androidx.lifecycle.ViewModel
import com.gominitta.android.presentation.mypage.model.MyPageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val repository: MyPageRepository,
) : ViewModel() {
    val nickname: String get() = repository.getNickname()
    val email: String get() = repository.getEmail()
    val profileImageIndex: Int get() = repository.getProfileImageIndex()
}