package com.gominitta.android.presentation.session

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gominitta.android.R
import com.gominitta.android.ui.components.GominittaButton
import com.gominitta.android.ui.components.GominittaButtonVariant
import com.gominitta.android.ui.components.GominittaCard
import com.gominitta.android.ui.components.GominittaCardVariant
import com.gominitta.android.ui.components.GominittaElevatedCard
import com.gominitta.android.ui.theme.AccentCream200
import com.gominitta.android.ui.theme.Body1_16m
import com.gominitta.android.ui.theme.Body3_14r
import com.gominitta.android.ui.theme.Gray200
import com.gominitta.android.ui.theme.Gray400
import com.gominitta.android.ui.theme.Gray600
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Heading3_20m
import com.gominitta.android.ui.theme.Primary200
import com.gominitta.android.ui.theme.Primary800
import com.gominitta.android.ui.theme.Title1_20sb
import com.gominitta.android.ui.theme.White800
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.abs

/**
 * 예약된 걱정 수정 (C105) — 마음 세션 목록의 "수정/삭제"에서 진입.
 * API 연동 전, UI 확인용 fake 데이터만 사용 — [sessionId] 는 실제 조회에는 아직 안 쓰인다.
 * 시간 선택 바텀시트는 Figma대로 월·일·시·분·AM/PM 5열이 각각 스크롤·스냅되는 휠 피커.
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
    var startDateTime by remember { mutableStateOf(EditableDateTime(4, 13, 9, 0, "PM")) }
    var endDateTime by remember { mutableStateOf(EditableDateTime(4, 13, 10, 0, "PM")) }
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
            Spacer(Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TimeEditCard(
                    dateLabel = startDateTime.toDateLabel(),
                    timeLabel = startDateTime.toTimeLabel(),
                    suffix = "부터",
                    variant = if (editingSlot == TimeSlot.Start) GominittaCardVariant.Type2 else GominittaCardVariant.Type1,
                    onClick = { editingSlot = TimeSlot.Start },
                    modifier = Modifier.weight(1f),
                )
                TimeEditCard(
                    dateLabel = endDateTime.toDateLabel(),
                    timeLabel = endDateTime.toTimeLabel(),
                    suffix = "까지",
                    variant = if (editingSlot == TimeSlot.End) GominittaCardVariant.Type2 else GominittaCardVariant.Type1,
                    onClick = { editingSlot = TimeSlot.End },
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(44.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                GominittaElevatedCard(modifier = Modifier.height(170.dp)) {
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
            containerColor = Primary200,
            shape = RectangleShape,
            contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
        ) {
            TimePickerSheetContent(
                initial = if (slot == TimeSlot.Start) startDateTime else endDateTime,
                onConfirm = { picked ->
                    if (slot == TimeSlot.Start) startDateTime = picked else endDateTime = picked
                    isDirty = true
                    editingSlot = null
                },
            )
        }
    }
}

private enum class TimeSlot { Start, End }

private data class EditableDateTime(
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val amPm: String,
)

private val KoreanWeekdays = arrayOf("월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일")

/** 실제 서버 연동 전이라 UI 확인용으로 2026년 기준 고정 연도를 사용한다. */
private fun EditableDateTime.toDateLabel(): String {
    val safeDay = day.coerceAtMost(YearMonth.of(2026, month).lengthOfMonth())
    val weekday = KoreanWeekdays[LocalDate.of(2026, month, safeDay).dayOfWeek.value - 1]
    return "%02d/%02d %s".format(month, safeDay, weekday)
}

private fun EditableDateTime.toTimeLabel(): String = "%02d:%02d %s".format(hour, minute, amPm)

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
                    .offset(x = 9.dp, y = (-12).dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(AccentCream200),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_pencil),
                    contentDescription = "시간 수정",
                    tint = Primary800,
                    modifier = Modifier.size(24.dp),
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

// ---- 휠 피커 (Figma C105-1: 월·일 / 시·분 / AM·PM 이 각각 스크롤·스냅되는 5열) ------------

private val WheelItemHeight = 40.dp
private const val WheelVisibleCount = 3
private val SheetHeight = 329.dp
private val SheetBorderWidth = 0.5.dp

private val MonthOptions = (1..12).toList()
private val DayOptions = (1..31).toList()
private val HourOptions = (1..12).toList()
private val MinuteOptions = (0..55 step 5).toList()
private val AmPmOptions = listOf("AM", "PM")

@Composable
private fun TimePickerSheetContent(initial: EditableDateTime, onConfirm: (EditableDateTime) -> Unit) {
    var month by remember { mutableStateOf(initial.month) }
    var day by remember { mutableStateOf(initial.day) }
    var hour by remember { mutableStateOf(initial.hour) }
    var minute by remember { mutableStateOf(initial.minute) }
    var amPm by remember { mutableStateOf(initial.amPm) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(SheetHeight)
            .border(SheetBorderWidth, Gray200)
            .padding(horizontal = 20.dp)
            .padding(bottom = 44.dp, top = 53.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(WheelItemHeight * WheelVisibleCount),
            contentAlignment = Alignment.Center,
        ) {
            // 가운데 선택 행 강조 배경 — 5열 전체에 걸쳐 한 줄로.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(WheelItemHeight)
                    .clip(RoundedCornerShape(10.dp))
                    .background(White800.copy(alpha = 0.5f)),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                WheelColumn(items = MonthOptions, selectedIndex = MonthOptions.indexOf(month), onSelect = { month = MonthOptions[it] }, label = { "${it}월" })
                WheelColumn(items = DayOptions, selectedIndex = DayOptions.indexOf(day), onSelect = { day = DayOptions[it] }, label = { "${it}일" })
                Spacer(Modifier.width(20.dp))
                WheelColumn(items = HourOptions, selectedIndex = HourOptions.indexOf(hour), onSelect = { hour = HourOptions[it] }, label = { "$it" })
                WheelColumn(items = MinuteOptions, selectedIndex = MinuteOptions.indexOf(minute), onSelect = { minute = MinuteOptions[it] }, label = { "%02d".format(it) })
                Spacer(Modifier.width(20.dp))
                WheelColumn(items = AmPmOptions, selectedIndex = AmPmOptions.indexOf(amPm), onSelect = { amPm = AmPmOptions[it] }, label = { it })
            }
        }

        Spacer(Modifier.height(52.dp))
        GominittaButton(
            text = "설정하기",
            onClick = { onConfirm(EditableDateTime(month, day, hour, minute, amPm)) },
            variant = GominittaButtonVariant.Primary,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun <T> WheelColumn(
    items: List<T>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    label: (T) -> String,
    modifier: Modifier = Modifier,
) {
    val startIndex = selectedIndex.coerceIn(items.indices)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex)
    val sidePadding = WheelItemHeight * (WheelVisibleCount / 2)

    val centeredIndex by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            if (info.visibleItemsInfo.isEmpty()) return@derivedStateOf startIndex
            val center = (info.viewportStartOffset + info.viewportEndOffset) / 2
            info.visibleItemsInfo.minBy { abs((it.offset + it.size / 2) - center) }.index
        }
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            onSelect(centeredIndex.coerceIn(items.indices))
        }
    }

    LazyColumn(
        state = listState,
        flingBehavior = rememberSnapFlingBehavior(listState),
        modifier = modifier
            .width(56.dp)
            .height(WheelItemHeight * WheelVisibleCount),
        contentPadding = PaddingValues(vertical = sidePadding),
    ) {
        itemsIndexed(items) { index, item ->
            val isSelected = index == centeredIndex
            Box(
                modifier = Modifier
                    .height(WheelItemHeight)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label(item),
                    style = Heading3_20m,
                    color = if (isSelected) Gray800 else Gray400,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

// ---- Fake data (API 연동 전) --------------------------------------------------

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
