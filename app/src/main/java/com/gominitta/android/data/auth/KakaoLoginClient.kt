package com.gominitta.android.data.auth

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken

/** 카카오 로그인 SDK 격리 seam. 카톡/계정 분기·취소 처리·콜백→코루틴 변환을 캡슐화. */
interface KakaoLoginClient {
    /** 성공 시 [OAuthToken], 사용자 취소 시 [LoginCancelledException], 그 외 실패 시 원인 예외를 throw. */
    suspend fun login(context: Context): OAuthToken

    /** 카카오 계정↔앱 연결(동의)을 해제한다. 성공 시 정상 반환, 실패 시 원인 예외를 throw. */
    suspend fun unlink()
}

/** 사용자가 로그인을 취소함 — 에러가 아니라 조용히 무시해야 하는 케이스. */
class LoginCancelledException : Exception()
