package com.gominitta.android.presentation.worry

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gominitta.android.presentation.worry.components.WorryExitDialog
import com.gominitta.android.presentation.worry.components.WorryMemoField
import com.gominitta.android.presentation.worry.components.WorryPrimaryButton
import com.gominitta.android.presentation.worry.components.WorryTopBar
import com.gominitta.android.ui.components.GominittaBackground
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Heading4_18m

/**
 * 한 줄 보태기 (B104) — 걱정 시간 예약 → 완료. 추가로 드는 생각을 한 줄 메모로 받는다.
 * 뒤로가기 시 작성 취소 확인 다이얼로그를 띄운다.
 */
@Composable
fun WorryMemoScreen(
    onNavigateNext: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var content by remember { mutableStateOf("") }
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = true) { showExitDialog = true }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                WorryTopBar(title = "한 줄 보태기", onBack = { showExitDialog = true })

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                ) {
                    Spacer(Modifier.height(36.dp))

                    Text(
                        text = "추가로 드는 생각이 있나요?",
                        style = Heading4_18m,
                        color = Gray800,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(56.dp))

                    WorryMemoField(
                        content = content,
                        onContentChange = { content = it },
                    )

                    Spacer(Modifier.height(96.dp))
                }
            }

            WorryPrimaryButton(
                text = "완료",
                onClick = onNavigateNext,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.ime.exclude(WindowInsets.navigationBars))
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = 28.dp),
                enabled = content.isNotBlank(),
            )
        }
    }

    if (showExitDialog) {
        WorryExitDialog(
            onConfirm = {
                showExitDialog = false
                onNavigateBack()
            },
            onDismiss = { showExitDialog = false },
        )
    }
}

// ---- Preview ---------------------------------------------------------------

@Preview(name = "WorryMemo", showBackground = true, backgroundColor = 0xFFFEFEFB, widthDp = 375, heightDp = 812)
@Composable
private fun WorryMemoScreenPreview() {
    GominittaTheme {
        GominittaBackground {
            WorryMemoScreen(onNavigateNext = {}, onNavigateBack = {})
        }
    }
}
