package com.gominitta.android.presentation.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.gominitta.android.ui.theme.spacing

/**
 * 리포트 — 하단탭: 리포트 (미설계).
 * TODO: 실제 UI 구현 (현재는 스텁).
 */
@Composable
fun ReportScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(MaterialTheme.spacing.md),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "리포트",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
            )
            TextButton(
                onClick = onNavigateBack,
                modifier = Modifier.padding(top = MaterialTheme.spacing.lg),
            ) { Text("← 뒤로") }
        }
    }
}
