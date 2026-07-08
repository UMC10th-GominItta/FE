package com.gominitta.android.presentation.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.gominitta.android.ui.theme.spacing

/**
 * 마음 세션 상세 · 인트로 — 홈 다음세션 카드 · 세션목록 항목.
 * TODO: 실제 UI 구현 (현재는 스텁).
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(MaterialTheme.spacing.md),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "마음 세션 상세 · 인트로",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
            )
            Button(
                onClick = onStartSession,
                modifier = Modifier.padding(top = MaterialTheme.spacing.lg),
            ) { Text("마음 세션 시작하기") }
            Spacer(Modifier.height(MaterialTheme.spacing.sm))
            TextButton(onClick = onSkip) { Text("건너뛰기") }
        }
    }
}
