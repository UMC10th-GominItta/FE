package com.gominitta.android.presentation.session

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * 마음 세션 상세 — 용도 미정 (인트로는 아님, [SessionActiveScreen] 쪽 바텀시트가 그 역할).
 * "세션 시작하기" 진입점(홈 다음세션 카드 · 세션목록 항목)은 이제 이 화면을 거치지 않고
 * [SessionActiveScreen] 으로 바로 이동한다 — 라우트/컴포저블은 나중에 실제 용도가 정해지면
 * 다시 채운다.
 */
@Composable
fun SessionDetailScreen(
    onStartSession: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) { }
}