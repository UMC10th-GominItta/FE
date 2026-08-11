package com.gominitta.android

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.gominitta.android.BuildConfig
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application entry-point.
 *
 * @HiltAndroidApp triggers Hilt's code generation and sets up the
 * application-level component. All other Hilt components descend from here.
 *
 * WorkManager의 매니페스트 자동 초기화는 꺼뒀다(AndroidManifest.xml 참고) — Hilt로
 * Worker에 의존성을 주입하려면 [HiltWorkerFactory]를 물린 Configuration으로 직접
 * 초기화해야 한다.
 */
@HiltAndroidApp
class GominittaApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        WorkManager.initialize(this, workManagerConfiguration)
    }
}
