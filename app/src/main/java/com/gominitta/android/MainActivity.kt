package com.gominitta.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gominitta.android.navigation.AppNavHost
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GominittaTheme(darkTheme = false) {   // 라이트 크림 단일 디자인 — 다크 스킴 미사용
                GominittaBackground {
                    AppNavHost()
                }
            }
        }
    }
}
