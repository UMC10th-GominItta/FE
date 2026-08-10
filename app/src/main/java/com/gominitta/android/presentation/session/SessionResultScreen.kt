package com.gominitta.android.presentation.session

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gominitta.android.R
import com.gominitta.android.domain.model.session.RecordType
import com.gominitta.android.domain.model.session.SessionRecord
import com.gominitta.android.ui.components.GominittaElevatedCard
import com.gominitta.android.ui.theme.Body1_16m
import com.gominitta.android.ui.theme.Body2_15r
import com.gominitta.android.ui.theme.Body3_14r
import com.gominitta.android.ui.theme.Gray400
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Heading5_15m
import com.gominitta.android.ui.theme.Primary800
import com.gominitta.android.ui.theme.Title1_20sb
import java.time.LocalDateTime

/**
 * 완료된 세션 상세 보기 (C103 확인 화면 참고) — 마음 세션 목록의 "완료 세션" 탭에서
 * 카드를 눌러 진입. GET /api/v1/sessions/{sessionId} 로 불러온 걱정 내용·기록을 읽기 전용으로 보여준다.
 */
@Composable
fun SessionResultScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SessionResultViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) { innerPadding ->
        SessionResultContent(
            innerPadding = innerPadding,
            isLoading = uiState.isLoading,
            worryContent = uiState.worryContent,
            records = uiState.records,
            errorMessage = uiState.errorMessage,
            onNavigateBack = onNavigateBack,
        )
    }
}

@Composable
private fun SessionResultContent(
    innerPadding: PaddingValues,
    isLoading: Boolean,
    worryContent: String,
    records: List<SessionRecord>,
    errorMessage: String?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)
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
                color = Gray800,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
            )
        }
        Spacer(Modifier.height(26.dp))

        when {
            isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary800)
            }
            errorMessage != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = errorMessage, style = Body2_15r, color = Gray800, textAlign = TextAlign.Center)
            }
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(text = "예약된 걱정", style = Heading5_15m, color = Gray800)
                Spacer(Modifier.height(8.dp))
                GominittaElevatedCard {
                    Text(text = worryContent, style = Body1_16m, color = Gray800)
                }
                Spacer(Modifier.height(20.dp))

                Text(text = "기록한 마음 세션", style = Heading5_15m, color = Gray800)
                Spacer(Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    GominittaElevatedCard {
                        Column(modifier = Modifier.heightIn(min = 160.dp)) {
                            if (records.isEmpty()) {
                                Text(
                                    text = "기록된 내용이 없어요.",
                                    style = Body2_15r,
                                    color = Gray400,
                                )
                            } else {
                                records.forEachIndexed { index, record ->
                                    if (index > 0) Spacer(Modifier.height(12.dp))
                                    SessionRecordItem(record)
                                }
                            }
                        }
                    }
                    WashiTapeDecoration(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = -(TapeSize.height / 2)),
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionRecordItem(record: SessionRecord, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = record.recordType.toLabel(), style = Body3_14r, color = Gray400)
        Spacer(Modifier.height(4.dp))
        Text(text = record.contentText, style = Body2_15r, color = Gray800)
    }
}

private fun RecordType.toLabel(): String = when (this) {
    RecordType.TEXT -> "텍스트"
    RecordType.VOICE -> "음성"
    RecordType.HANDWRITING -> "필기"
}

// ---- Preview ---------------------------------------------------------------

@Preview(name = "SessionResult - 기록 있음", showBackground = true, backgroundColor = 0xFFF3F0EB)
@Composable
private fun SessionResultContentPreview() {
    GominittaTheme {
        SessionResultContent(
            innerPadding = PaddingValues(0.dp),
            isLoading = false,
            worryContent = "UMC 프론트가 안 구해지면 어떡하지\n걱정걱정걱정",
            records = listOf(
                SessionRecord(
                    id = 1L,
                    recordType = RecordType.TEXT,
                    contentText = "생각보다 별거 아니었다.",
                    mediaUrl = null,
                    createdAt = LocalDateTime.now(),
                ),
            ),
            errorMessage = null,
            onNavigateBack = {},
        )
    }
}

@Preview(name = "SessionResult - 기록 없음", showBackground = true, backgroundColor = 0xFFF3F0EB)
@Composable
private fun SessionResultContentEmptyPreview() {
    GominittaTheme {
        SessionResultContent(
            innerPadding = PaddingValues(0.dp),
            isLoading = false,
            worryContent = "UMC 프론트가 안 구해지면 어떡하지",
            records = emptyList(),
            errorMessage = null,
            onNavigateBack = {},
        )
    }
}
