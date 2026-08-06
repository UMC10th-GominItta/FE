package com.gominitta.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gominitta.android.navigation.AppNavHost
import com.gominitta.android.presentation.MainViewModel
import com.gominitta.android.ui.components.GominittaBackground
import com.gominitta.android.ui.theme.GominittaTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-activity shell. Hosts the Compose UI tree.
 *
 * @AndroidEntryPoint enables Hilt injection into this activity.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        splashScreen.setKeepOnScreenCondition { viewModel.startDestination.value == null }
        setContent {
            GominittaTheme(darkTheme = false) {   // 라이트 크림 단일 디자인 — 다크 스킴 미사용
                GominittaBackground {
                    val startDestination by viewModel.startDestination.collectAsStateWithLifecycle()
                    startDestination?.let { destination ->
                        AppNavHost(startDestination = destination)
                    }
                }
            }
        }
    }
}
