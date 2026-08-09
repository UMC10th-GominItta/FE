package com.gominitta.android.presentation.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.usecase.GetUserProfileUseCase
import com.gominitta.android.presentation.mypage.model.ProfileImages
import com.gominitta.android.presentation.mypage.toProfileIndex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileImageViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
) : ViewModel() {

    var profileIndex: Int? by mutableStateOf(null)
        private set

    init {
        viewModelScope.launch {
            try {
                val user = getUserProfileUseCase()
                profileIndex = user.profileImageUrl.toProfileIndex()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // 로딩 중(null)엔 빈 상태를 보여주지만, 조회 자체가 실패로 확정되면
                // 화면이 계속 비어있지 않도록 기본 캐릭터로 대체한다.
                profileIndex = ProfileImages.DEFAULT_INDEX
            }
        }
    }
}
