package com.gominitta.android.presentation.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gominitta.android.R
import com.gominitta.android.ui.theme.Body2_15r
import com.gominitta.android.ui.theme.Gray600
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Heading1_24sb
import com.gominitta.android.ui.theme.Primary800
import kotlinx.coroutines.delay

private const val AUTO_ADVANCE_DELAY_MS = 1800L

/**
 * 마음 세션 완료 (C104-1) — 세션 진행 → 완료. 안내만 잠깐 보여주고 자동으로 평가 화면으로 넘어간다
 * (LoginCompleteScreen 과 동일한 패턴). 버튼 없음 — 목업에도 없음.
 */
@Composable
fun SessionCompleteScreen(
    onNavigateNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        delay(AUTO_ADVANCE_DELAY_MS)
        onNavigateNext()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_finish),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(width = 58.dp, height = 64.dp),
            )
            Spacer(Modifier.height(24.dp))
            Text(text = "마음 세션 완료", style = Heading1_24sb, color = Primary800, textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))
            Text(
                text = "걱정을 피하지 않고 마주한 것만으로도\n마음 건강에 도움이 되는 일이에요.",
                style = Body2_15r,
                color = Gray600,
                textAlign = TextAlign.Center,
            )
        }
    }
}

// ---- Preview ---------------------------------------------------------------

@Preview(name = "SessionComplete", showBackground = true, backgroundColor = 0xFFF3F0EB)
@Composable
private fun SessionCompleteScreenPreview() {
    GominittaTheme {
        SessionCompleteScreen(onNavigateNext = {})
    }
}
