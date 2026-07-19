package com.gominitta.android.presentation.session

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.gominitta.android.ui.components.GominittaCard
import com.gominitta.android.ui.components.GominittaCardVariant
import com.gominitta.android.ui.components.GominittaElevatedCard
import com.gominitta.android.ui.theme.Body1_16m
import com.gominitta.android.ui.theme.Body3_14r
import com.gominitta.android.ui.theme.Gray400
import com.gominitta.android.ui.theme.Gray600
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Primary800
import com.gominitta.android.ui.theme.Title1_20sb
import com.gominitta.android.ui.theme.White800

/**
 * 예약된 걱정 수정 (C105) — 마음 세션 목록의 "수정/삭제"에서 진입.
 * API 연동 전, UI 확인용 fake 데이터만 사용 — [sessionId] 는 실제 조회에는 아직 안 쓰인다.
 * 시간 선택 바텀시트는 Figma의 휠 피커 대신 탭으로 고르는 리스트로 단순화했다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionEditScreen(


    sessionId: Long,
    onNavigateBack: () -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var memoText by remember { mutableStateOf(FAKE_MEMO) }
    var startTime by remember { mutableStateOf("09:00 PM") }
    var endTime by remember { mutableStateOf("10:00 PM") }
    var isDirty by remember { mutableStateOf(false) }
    var editingSlot by remember { mutableStateOf<TimeSlot?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) { innerPadding ->
        Column(
            modifier = Modifier
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
                    text = "예약된 걱정 수정",
                    style = Title1_20sb,
                    color = Primary800,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center),
                )
                Icon(
                    painter = painterResource(R.drawable.ic_trash),
                    contentDescription = "삭제",
                    tint = Primary800,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(24.dp)
                        .clickable(onClick = onDelete),
                )
            }
            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TimeEditCard(
                    dateLabel = FAKE_DATE_LABEL,
                    timeLabel = startTime,
                    suffix = "부터",
                    variant = GominittaCardVariant.Type2,
                    onClick = { editingSlot = TimeSlot.Start },
                    modifier = Modifier.weight(1f),
                )
                TimeEditCard(
                    dateLabel = FAKE_DATE_LABEL,
                    timeLabel = endTime,
                    suffix = "까지",
                    variant = GominittaCardVariant.Type1,
                    onClick = { editingSlot = TimeSlot.End },
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(20.dp))

            GominittaElevatedCard {
                Text(text = FAKE_WORRY_TITLE, style = Body1_16m, color = Primary800)
                Spacer(Modifier.height(8.dp))
                BasicTextField(
                    value = memoText,
                    onValueChange = {
                        memoText = it
                        isDirty = true
                    },
                    textStyle = Body3_14r.copy(color = Gray600),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.weight(1f))
            GominittaButton(
                text = "저장하기",
                onClick = onSave,
                enabled = isDirty,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    val slot = editingSlot
    if (slot != null) {
        ModalBottomSheet(
            onDismissRequest = { editingSlot = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            TimePickerSheetContent(
                initialValue = if (slot == TimeSlot.Start) startTime else endTime,
                onConfirm = { picked ->
                    if (slot == TimeSlot.Start) startTime = picked else endTime = picked
                    isDirty = true
                    editingSlot = null
                },
            )
        }
    }
}

private enum class TimeSlot { Start, End }

@Composable
private fun TimeEditCard(
    dateLabel: String,
    timeLabel: String,
    suffix: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: GominittaCardVariant = GominittaCardVariant.Type1,
) {
    GominittaCard(modifier = modifier.clickable(onClick = onClick), variant = variant) {
        Box(Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(White800),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_pencil),
                    contentDescription = "시간 수정",
                    tint = Primary800,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
        Text(text = dateLabel, style = Body3_14r, color = Gray600)
        Spacer(Modifier.height(4.dp))
        Row {
            Text(text = timeLabel, style = Body1_16m, color = Primary800)
            Spacer(Modifier.width(4.dp))
            Text(text = suffix, style = Body3_14r, color = Gray400)
        }
    }
}

// TODO: Figma는 월/일, 시/분, AM·PM 3열이 각각 스크롤·스냅되는 휠 피커. 지금은 탭으로 고르는
// 리스트로 단순화해뒀다 — 나중에 실제 휠 피커 컴포넌트로 교체할 것.
@Composable
private fun TimePickerSheetContent(initialValue: String, onConfirm: (String) -> Unit) {
    val options = listOf("09:00 PM", "09:30 PM", "10:00 PM", "10:30 PM", "11:00 PM")
    var selected by remember { mutableStateOf(initialValue) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp, top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "시간 설정", style = Title1_20sb, color = Primary800)
        Spacer(Modifier.height(16.dp))
        options.forEach { option ->
            val isSelected = option == selected
            Text(
                text = option,
                style = if (isSelected) Body1_16m else Body3_14r,
                color = if (isSelected) Primary800 else Gray400,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selected = option }
                    .padding(vertical = 10.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        GominittaButton(
            text = "설정하기",
            onClick = { onConfirm(selected) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ---- Fake data (API 연동 전) --------------------------------------------------

private const val FAKE_DATE_LABEL = "04/13 월요일"
private const val FAKE_WORRY_TITLE = "UMC 프론트가 안 구해지면 어떡하지"
private const val FAKE_MEMO = "걱정걱정걱정"

// ---- Preview ---------------------------------------------------------------

@Preview(name = "SessionEdit", showBackground = true, backgroundColor = 0xFFF3F0EB)
@Composable
private fun SessionEditScreenPreview() {
    GominittaTheme {
        SessionEditScreen(
            sessionId = 1L,
            onNavigateBack = {},
            onSave = {},
            onDelete = {},
        )
    }
}
