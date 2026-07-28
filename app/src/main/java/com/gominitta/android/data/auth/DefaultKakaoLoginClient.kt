package com.gominitta.android.data.auth

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

/** [KakaoLoginClient]의 실제 구현 — 카카오 SDK 콜백을 코루틴으로 감싼다. */
class DefaultKakaoLoginClient @Inject constructor() : KakaoLoginClient {

    override suspend fun login(context: Context): OAuthToken =
        suspendCancellableCoroutine { continuation ->
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                    when {
                        error != null && error is ClientError && error.reason == ClientErrorCause.Cancelled -> {
                            continuation.resumeWithException(LoginCancelledException())
                        }
                        error != null -> {
                            UserApiClient.instance.loginWithKakaoAccount(context) { accountToken, accountError ->
                                if (accountError != null) {
                                    continuation.resumeWithException(accountError)
                                } else if (accountToken != null) {
                                    continuation.resume(accountToken)
                                }
                            }
                        }
                        token != null -> continuation.resume(token)
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
                    if (error != null) {
                        continuation.resumeWithException(error)
                    } else if (token != null) {
                        continuation.resume(token)
                    }
                }
            }
        }
}
