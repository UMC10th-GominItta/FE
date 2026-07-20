package com.gominitta.android.presentation.session

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gominitta.android.R
import com.gominitta.android.ui.components.GominittaButton
import com.gominitta.android.ui.theme.Body1_16m
import com.gominitta.android.ui.theme.Body2_15r
import com.gominitta.android.ui.theme.Gray600
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Primary200
import com.gominitta.android.ui.theme.Primary800
import com.gominitta.android.ui.theme.Title1_20sb

/**
 * 마음 세션 기록 확인 (C103 후속, Figma c103-3) — 세션 기록 → 기록 내용 확인 → 세션 완료.
 * 방금 기록한 내용을 다시 보여주고 눌러서 바로 고칠 수 있게 한다. 지금은 API 연동 전이라
 * SessionActiveScreen에서 입력한 텍스트를 실제로 넘겨받지 않고 더미 텍스트로 미리 채워둔다.
 */
@Composable
fun SessionDetailScreen(
    onNavigateBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    initialText: String = FAKE_RECORDED_TEXT,
) {
    var recordText by remember { mutableStateOf(initialText) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 24.dp),
        ) {
            Box(Modifier.fillMaxWidth()) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = "뒤로",
                    tint = Primary800,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(24.dp)
                        .clickable(onClick = onNavigateBack),
                )
                Text(
                    text = "마음 세션",
                    style = Title1_20sb,
                    color = Primary800,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            Spacer(Modifier.height(24.dp))

            Text(text = "방금 기록된 내용이에요!", style = Body1_16m, color = Primary800)
            Spacer(Modifier.height(4.dp))
            Text(
                text = "틀린 부분이 있다면 눌러서 수정해 보세요.",
                style = Body2_15r,
                color = Gray600,
            )
            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Primary200)
                    .padding(16.dp),
            ) {
                BasicTextField(
                    value = recordText,
                    onValueChange = { recordText = it },
                    textStyle = Body2_15r.copy(color = Primary800),
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Spacer(Modifier.height(24.dp))

            GominittaButton(
                text = "저장",
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ---- Fake data (API 연동 전) --------------------------------------------------

private const val FAKE_RECORDED_TEXT = "입력된 내용"

// ---- Preview ---------------------------------------------------------------

@Preview(name = "SessionDetail - 기록 확인", showBackground = true, backgroundColor = 0xFFF3F0EB)
@Composable
private fun SessionDetailScreenPreview() {
    GominittaTheme {
        SessionDetailScreen(onNavigateBack = {}, onSave = {})
    }
}
