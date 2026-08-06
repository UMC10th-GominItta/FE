package com.gominitta.android.presentation.onboarding

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.data.auth.KakaoLoginClient
import com.gominitta.android.data.auth.LoginCancelledException
import com.gominitta.android.domain.usecase.LoginWithKakaoUseCase
import com.kakao.sdk.common.model.ApiError
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.KakaoSdkError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val kakaoLoginClient: KakaoLoginClient,
    private val loginWithKakao: LoginWithKakaoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _loginSuccess = Channel<Unit>(Channel.BUFFERED)
    val loginSuccess = _loginSuccess.receiveAsFlow()

    fun login(context: Context) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val token = kakaoLoginClient.login(context)
                loginWithKakao(token.accessToken)
                _uiState.update { it.copy(isLoading = false) }
                _loginSuccess.send(Unit)
            } catch (e: LoginCancelledException) {
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "로그인 실패 (${loginErrorDetail(e)})")
                }
            }
        }
    }

    fun errorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun loginErrorDetail(e: Throwable): String = when (e) {
        is AuthError -> "AuthError statusCode=${e.statusCode} reason=${e.reason} " +
            "error=${e.response.error} description=${e.response.errorDescription}"
        is ApiError -> "ApiError statusCode=${e.statusCode} reason=${e.reason} " +
            "code=${e.response.code} msg=${e.response.msg}"
        is ClientError -> "ClientError reason=${e.reason} msg=${e.msg}"
        is KakaoSdkError -> e.msg
        else -> e.message ?: e::class.simpleName ?: "Unknown"
    }
}
