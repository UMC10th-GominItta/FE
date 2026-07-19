package com.gominitta.android.presentation.session

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gominitta.android.R
import com.gominitta.android.domain.model.session.SessionStatus
import com.gominitta.android.domain.model.session.SessionSummary
import com.gominitta.android.ui.components.GominittaButton
import com.gominitta.android.ui.components.GominittaButtonVariant
import com.gominitta.android.ui.components.GominittaElevatedCard
import com.gominitta.android.ui.theme.AccentCream100
import com.gominitta.android.ui.theme.Body1_16m
import com.gominitta.android.ui.theme.Body2_15r
import com.gominitta.android.ui.theme.Body3_14r
import com.gominitta.android.ui.theme.Gray400
import com.gominitta.android.ui.theme.Gray600
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Heading3_18m
import com.gominitta.android.ui.theme.Primary800
import com.gominitta.android.ui.theme.Title1_20sb
import java.time.LocalDateTime

/**
 * 마음 세션 목록 — 하단탭: 마음 세션 (C101). 예정된 세션 / 미완료 세션 두 섹션.
 * 각 카드의 "세션 시작"은 세션 ID를 실어 [onNavigateToSessionDetail] 로 전달한다.
 */
@Composable
fun SessionListScreen(
    onNavigateToSessionDetail: (Long) -> Unit,
    onNavigateToSessionEdit: (Long) -> Unit,
    onNavigateToWorryInput: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SessionListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) { innerPadding ->
        when {
            uiState.isLoading -> LoadingState(innerPadding)
            uiState.errorMessage != null -> ErrorState(
                innerPadding = innerPadding,
                message = uiState.errorMessage.orEmpty(),
                onRetry = viewModel::load,
            )
            else -> SessionListContent(
                innerPadding = innerPadding,
                scheduled = uiState.scheduled,
                incomplete = uiState.incomplete,
                onNavigateToSessionDetail = onNavigateToSessionDetail,
                onNavigateToSessionEdit = onNavigateToSessionEdit,
                onNavigateToWorryInput = onNavigateToWorryInput,
            )
        }
    }
}

@Composable
private fun LoadingState(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = Primary800)
    }
}

@Composable
private fun ErrorState(innerPadding: PaddingValues, message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(innerPadding).padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = message, style = Body2_15r, color = Gray600, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        GominittaButton(text = "다시 시도", onClick = onRetry)
    }
}

@Composable
private fun SessionListContent(
    innerPadding: PaddingValues,
    scheduled: List<SessionSummary>,
    incomplete: List<SessionSummary>,
    onNavigateToSessionDetail: (Long) -> Unit,
    onNavigateToSessionEdit: (Long) -> Unit,
    onNavigateToWorryInput: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "마음 세션",
            style = Title1_20sb,
            color = Primary800,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        )

        Text(text = "예정된 세션", style = Heading3_18m, color = Primary800)
        if (scheduled.isEmpty()) {
            EmptyScheduledCard(onNavigateToWorryInput)
        } else {
            scheduled.forEach { session ->
                SessionCard(
                    session = session,
                    onStartSession = onNavigateToSessionDetail,
                    onEditSession = onNavigateToSessionEdit,
                )
            }
        }

        Text(text = "미완료 세션", style = Heading3_18m, color = Primary800)
        if (incomplete.isEmpty()) {
            Text(
                text = "미완료된 세션이 없어요.",
                style = Body3_14r,
                color = Gray400,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            )
        } else {
            incomplete.forEach { session ->
                SessionCard(
                    session = session,
                    onStartSession = onNavigateToSessionDetail,
                    onEditSession = onNavigateToSessionEdit,
                )
            }
        }
    }
}

@Composable
private fun EmptyScheduledCard(onNavigateToWorryInput: () -> Unit) {
    GominittaElevatedCard {
        Text(
            text = "예정된 세션이 없어요.",
            style = Body2_15r,
            color = Gray400,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        GominittaButton(
            text = "걱정 예약하기",
            onClick = onNavigateToWorryInput,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(painterResource(R.drawable.ic_plus), null, Modifier.size(20.dp))
            },
        )
    }
}

@Composable
private fun SessionCard(
    session: SessionSummary,
    onStartSession: (Long) -> Unit,
    onEditSession: (Long) -> Unit,
) {
    GominittaElevatedCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentCream100),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_calendar),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Primary800,
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = session.scheduledStartAt.toSessionCardLabel(),
                    style = Body3_14r,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = "수정/삭제",
                style = Body3_14r,
                color = Gray400,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(onClick = { onEditSession(session.id) }),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(text = session.worryContent, style = Body1_16m, color = Primary800)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GominittaButton(
                text = "한 줄 보태기",
                onClick = {
                    // TODO: 걱정 메모 추가 API 연결
                },
                modifier = Modifier.weight(1f),
                variant = GominittaButtonVariant.Outlined,
                leadingIcon = {
                    Icon(painterResource(R.drawable.ic_chat), null, Modifier.size(18.dp))
                },
            )
            GominittaButton(
                text = "세션 시작",
                onClick = { onStartSession(session.id) },
                modifier = Modifier.weight(1f),
                leadingIcon = {
                    Icon(painterResource(R.drawable.ic_play), null, Modifier.size(18.dp))
                },
            )
        }
    }
}

// ---- 날짜 포맷 ---------------------------------------------------------------

private val koreanWeekdays = arrayOf("월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일")

/** "5월 27일 수요일 · 10:00 PM" 형식 (모큐업 표기와 동일하게 AM/PM은 영문 표기). */
private fun LocalDateTime.toSessionCardLabel(): String {
    val weekday = koreanWeekdays[dayOfWeek.value - 1]
    val hour12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    val amPm = if (hour < 12) "AM" else "PM"
    val minuteStr = minute.toString().padStart(2, '0')
    return "${monthValue}월 ${dayOfMonth}일 $weekday · $hour12:$minuteStr $amPm"
}

// ---- Preview ---------------------------------------------------------------

private val previewScheduled = listOf(
    SessionSummary(
        id = 1,
        worryId = 10,
        worryContent = "UMC 프론트가 안 구해지면 어떡하지",
        status = SessionStatus.SCHEDULED,
        scheduledStartAt = LocalDateTime.of(2026, 5, 27, 22, 0),
        scheduledEndAt = LocalDateTime.of(2026, 5, 27, 23, 0),
    ),
    SessionSummary(
        id = 2,
        worryId = 11,
        worryContent = "UMC 디자이너가 안 구해지면 어떡하지",
        status = SessionStatus.SCHEDULED,
        scheduledStartAt = LocalDateTime.of(2026, 5, 28, 23, 0),
        scheduledEndAt = LocalDateTime.of(2026, 5, 29, 0, 0),
    ),
)

private val previewIncomplete = listOf(
    SessionSummary(
        id = 3,
        worryId = 12,
        worryContent = "UMC 프론트가 안 구해지면 어떡하지",
        status = SessionStatus.INCOMPLETE,
        scheduledStartAt = LocalDateTime.of(2026, 5, 19, 23, 0),
        scheduledEndAt = LocalDateTime.of(2026, 5, 20, 0, 0),
    ),
)

@Preview(name = "SessionList - 채워진 상태", showBackground = true, backgroundColor = 0xFFF3F0EB)
@Composable
private fun SessionListContentPopulatedPreview() {
    GominittaTheme {
        SessionListContent(
            innerPadding = PaddingValues(0.dp),
            scheduled = previewScheduled,
            incomplete = previewIncomplete,
            onNavigateToSessionDetail = {},
            onNavigateToSessionEdit = {},
            onNavigateToWorryInput = {},
        )
    }
}

@Preview(name = "SessionList - 빈 상태", showBackground = true, backgroundColor = 0xFFF3F0EB)
@Composable
private fun SessionListContentEmptyPreview() {
    GominittaTheme {
        SessionListContent(
            innerPadding = PaddingValues(0.dp),
            scheduled = emptyList(),
            incomplete = emptyList(),
            onNavigateToSessionDetail = {},
            onNavigateToSessionEdit = {},
            onNavigateToWorryInput = {},
        )
    }
}
