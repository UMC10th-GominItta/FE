package com.gominitta.android

import android.app.Application
import com.gominitta.android.BuildConfig
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry-point.
 *
 * @HiltAndroidApp triggers Hilt's code generation and sets up the
 * application-level component. All other Hilt components descend from here.
 */
@HiltAndroidApp
class GominittaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }
}
