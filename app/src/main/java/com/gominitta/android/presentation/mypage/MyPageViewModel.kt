package com.gominitta.android.presentation.mypage

import androidx.lifecycle.ViewModel
import com.gominitta.android.presentation.mypage.model.DummyMyPageRepository
import com.gominitta.android.presentation.mypage.model.MyPageRepository

class MyPageViewModel @JvmOverloads constructor(
    private val repository: MyPageRepository = DummyMyPageRepository(),
) : ViewModel() {
    val nickname: String get() = repository.getNickname()
    val email: String get() = repository.getEmail()
}