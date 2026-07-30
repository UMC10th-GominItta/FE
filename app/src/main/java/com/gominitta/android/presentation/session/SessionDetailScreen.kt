package com.gominitta.android.presentation.session

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gominitta.android.R
import com.gominitta.android.ui.components.GominittaButton
import com.gominitta.android.ui.components.GominittaCard
import com.gominitta.android.ui.theme.Body1_16m
import com.gominitta.android.ui.theme.Body2_15r
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Primary800
import com.gominitta.android.ui.theme.Title1_20sb

/**
 * 마음 세션 기록 확인 (C103 "인식 내용 확인 및 텍스트 수정 영역", 확정 디자인) —
 * 세션 기록 → 기록 내용 확인 → 세션 완료. [SessionActiveScreen]에서 방금 기록한 [initialText]를
 * 다시 보여주고 눌러서 바로 고칠 수 있게 하며, "저장하기"를 누르면 [sessionId] 세션에 실제로 저장된다.
 * 카드 장식은 [SessionActiveScreen]의 텍스트 기록 카드와 동일한 [WashiTapeDecoration]을 재사용해
 * 두 화면의 카드 톤을 맞춘다.
 */
@Composable
fun SessionDetailScreen(
    sessionId: Long,
    initialText: String,
    onNavigateBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SessionDetailViewModel = hiltViewModel(),
) {
    var recordText by remember(sessionId) { mutableStateOf(initialText) }

    SessionDetailContent(
        recordText = recordText,
        onRecordTextChange = { recordText = it },
        onNavigateBack = onNavigateBack,
        onSaveClick = { viewModel.save(sessionId, recordText, onSaved = onSave) },
        modifier = modifier,
    )
}

@Composable
private fun SessionDetailContent(
    recordText: String,
    onRecordTextChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onSaveClick: () -> Unit,
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
            Spacer(Modifier.height(26.dp))

            Text(
                text = "방금 기록된 내용이에요.\n틀린 부분이 있다면 눌러서 수정해 보세요.",
                style = Body1_16m,
                color = Gray800,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
            )
            Spacer(Modifier.height(50.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                GominittaCard {
                    BasicTextField(
                        value = recordText,
                        onValueChange = onRecordTextChange,
                        textStyle = Body2_15r.copy(color = Primary800),
                        modifier = Modifier.fillMaxWidth().heightIn(min = 160.dp),
                    )
                }
                // 테이프 세로 중심을 카드 위쪽 가장자리에 맞춰 절반만 겹치게 offset.
                WashiTapeDecoration(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = -(TapeSize.height / 2)),
                )
            }
            Spacer(Modifier.weight(1f))

            GominittaButton(
                text = "저장하기",
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ---- Preview ---------------------------------------------------------------

@Preview(name = "SessionDetail - 기록 확인", showBackground = true, backgroundColor = 0xFFF3F0EB)
@Composable
private fun SessionDetailScreenPreview() {
    GominittaTheme {
        SessionDetailContent(
            recordText = "입력된 내용 입력된 내용 입력된 내용 입력된 내용 입력된 내용 " +
                "입력된 내용 입력된 내용 입력된 내용 입력된 내용 입력된 내용 입력된 내용 입력된 내용 " +
                "입력된 내용 입력된 내용 입력된 내용",
            onRecordTextChange = {},
            onNavigateBack = {},
            onSaveClick = {},
        )
    }
}
