package com.gominitta.android.presentation.home

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.gominitta.android.ui.theme.spacing

/**
 * 홈 — 하단 4탭 중 첫 탭. 인사말 + "오늘의 고민 보관하러 가기" · "다음 마음 세션" 진입.
 * 진입: 로그인 후 / 하단탭: 홈.
 *
 * ViewModel(+UseCase) 연동 예시가 그대로 남아있음 — 새 화면 작성 시 이 패턴 참고.
 * TODO: 실제 홈 UI + 하단 탭 바(BottomNavigationBar) 구현 (현재는 스텁).
 */
@Composable
fun HomeScreen(
    onNavigateToWorryInput: () -> Unit = {},
    onNavigateToSessionDetail: () -> Unit = {},
    onNavigateToSessionList: () -> Unit = {},
    onNavigateToRecipe: () -> Unit = {},
    onNavigateToReport: () -> Unit = {},
    onNavigateToMyPage: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val greeting by viewModel.greeting.collectAsState()

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
                text = "홈",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = greeting.ifBlank { "Scaffold ready" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = MaterialTheme.spacing.sm),
            )
            Spacer(Modifier.height(MaterialTheme.spacing.lg))
            Button(onClick = onNavigateToWorryInput) { Text("오늘의 고민 보관하러 가기") }
            Spacer(Modifier.height(MaterialTheme.spacing.sm))
            Button(onClick = onNavigateToSessionDetail) { Text("다음 마음 세션") }
            Spacer(Modifier.height(MaterialTheme.spacing.md))
            TextButton(onClick = onNavigateToSessionList) { Text("마음 세션") }
            TextButton(onClick = onNavigateToRecipe) { Text("마음 레시피") }
            TextButton(onClick = onNavigateToReport) { Text("리포트") }
            TextButton(onClick = onNavigateToMyPage) { Text("마이페이지") }
        }
    }
}
