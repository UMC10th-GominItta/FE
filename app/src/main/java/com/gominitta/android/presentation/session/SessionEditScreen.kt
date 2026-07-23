package com.gominitta.android.presentation.session

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material3.BottomSheetDefaults
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
import com.gominitta.android.ui.theme.Gray400
import com.gominitta.android.ui.theme.Gray600
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Heading3_20m
import com.gominitta.android.ui.theme.Heading4_18m
import com.gominitta.android.ui.theme.Heading5_15m
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

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
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
                        datePart = startDateTime.toDatePart(),
                        weekdayPart = startDateTime.toWeekdayPart(),
                        timeLabel = startDateTime.toTimeLabel(),
                        suffix = "부터",
                        variant = if (editingSlot == TimeSlot.Start) GominittaCardVariant.Type2 else GominittaCardVariant.Type1,
                        onClick = { editingSlot = TimeSlot.Start },
                        modifier = Modifier.weight(1f).height(111.dp),
                    )
                    TimeEditCard(
                        datePart = endDateTime.toDatePart(),
                        weekdayPart = endDateTime.toWeekdayPart(),
                        timeLabel = endDateTime.toTimeLabel(),
                        suffix = "까지",
                        variant = if (editingSlot == TimeSlot.End) GominittaCardVariant.Type2 else GominittaCardVariant.Type1,
                        onClick = { editingSlot = TimeSlot.End },
                        modifier = Modifier.weight(1f).height(111.dp),
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

        if (editingSlot != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.BottomCenter)
                    .background(SheetScrimGradient),
            )
        }
    }

    val slot = editingSlot
    if (slot != null) {
        ModalBottomSheet(
            onDismissRequest = { editingSlot = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Primary200,
            contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
            scrimColor = Color.Transparent,
            dragHandle = { BottomSheetDefaults.DragHandle(width = 50.dp) },
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
private fun EditableDateTime.toDatePart(): String {
    val safeDay = day.coerceAtMost(YearMonth.of(2026, month).lengthOfMonth())
    return "%02d/%02d".format(month, safeDay)
}

private fun EditableDateTime.toWeekdayPart(): String {
    val safeDay = day.coerceAtMost(YearMonth.of(2026, month).lengthOfMonth())
    return KoreanWeekdays[LocalDate.of(2026, month, safeDay).dayOfWeek.value - 1]
}

private fun EditableDateTime.toTimeLabel(): String = "%02d:%02d %s".format(hour, minute, amPm)

@Composable
private fun TimeEditCard(
    datePart: String,
    weekdayPart: String,
    timeLabel: String,
    suffix: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: GominittaCardVariant = GominittaCardVariant.Type1,
) {
    Box(modifier = modifier) {
        GominittaCard(modifier = Modifier.fillMaxSize().clickable(onClick = onClick), variant = variant) {
            Spacer(Modifier.height(20.29.dp))
            Row {
                Text(text = datePart, style = Heading4_18m, color = Gray800, modifier = Modifier.alignByBaseline())
                Spacer(Modifier.width(4.dp))
                Text(text = weekdayPart, style = Heading5_15m, color = Gray800, modifier = Modifier.alignByBaseline())
            }
            Spacer(Modifier.height(4.dp))
            Row {
                Text(text = timeLabel, style = Heading4_18m, color = Gray800, modifier = Modifier.alignByBaseline())
                Spacer(Modifier.width(4.dp))
                Text(text = suffix, style = Heading5_15m, color = Gray800, modifier = Modifier.alignByBaseline())
            }
        }
        // 배지는 카드 콘텐츠 흐름 밖에서 카드 가장자리 기준(위 8dp·오른쪽 11dp)으로 겹쳐 그린다.
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-11).dp, y = 8.dp)
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
}

// ---- 휠 피커 (Figma C105-1: 월·일 / 시·분 / AM·PM 이 각각 스크롤·스냅되는 5열) ------------

private val WheelItemHeight = 40.dp
private const val WheelVisibleCount = 3

private val MonthOptions = (1..12).toList()
private val HourOptions = (1..12).toList()
private val MinuteOptions = (0..55 step 5).toList()
private val AmPmOptions = listOf("AM", "PM")

@Composable
private fun TimePickerSheetContent(initial: EditableDateTime, onConfirm: (EditableDateTime) -> Unit) {
    var current by remember { mutableStateOf(initial) }
    val dayOptions = remember(current.month) {
        (1..YearMonth.of(2026, current.month).lengthOfMonth()).toList()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(281.dp) // 329 - 드래그 핸들 영역(48dp)
            .padding(horizontal = 20.dp)
            .padding(bottom = 44.dp, top = 6.dp),
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
                WheelColumn(
                    items = MonthOptions,
                    selectedIndex = MonthOptions.indexOf(current.month),
                    onSelect = { index ->
                        val newMonth = MonthOptions[index]
                        val maxDay = YearMonth.of(2026, newMonth).lengthOfMonth()
                        current = current.copy(month = newMonth, day = current.day.coerceAtMost(maxDay))
                    },
                    label = { "${it}월" },
                )
                WheelColumn(
                    items = dayOptions,
                    selectedIndex = dayOptions.indexOf(current.day),
                    onSelect = { index -> current = current.copy(day = dayOptions[index]) },
                    label = { "${it}일" },
                )
                Spacer(Modifier.width(20.dp))
                WheelColumn(
                    items = HourOptions,
                    selectedIndex = HourOptions.indexOf(current.hour),
                    onSelect = { index -> current = current.copy(hour = HourOptions[index]) },
                    label = { "$it" },
                )
                WheelColumn(
                    items = MinuteOptions,
                    selectedIndex = MinuteOptions.indexOf(current.minute),
                    onSelect = { index -> current = current.copy(minute = MinuteOptions[index]) },
                    label = { "%02d".format(it) },
                )
                Spacer(Modifier.width(20.dp))
                WheelColumn(
                    items = AmPmOptions,
                    selectedIndex = AmPmOptions.indexOf(current.amPm),
                    onSelect = { index -> current = current.copy(amPm = AmPmOptions[index]) },
                    label = { it },
                )
            }
        }

        Spacer(Modifier.weight(1f))
        GominittaButton(
            text = "설정하기",
            onClick = { onConfirm(current) },
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

    // 다른 휠(예: 월)의 변경으로 selectedIndex 가 바깥에서 바뀌었을 때(예: 일자 클램핑),
    // 스크롤 위치를 새 값에 맞춰 따라가게 한다.
    LaunchedEffect(startIndex, items) {
        if (!listState.isScrollInProgress && centeredIndex != startIndex) {
            listState.scrollToItem(startIndex)
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
            val color = if (isSelected) Gray800 else Gray400
            Box(
                modifier = Modifier
                    .height(WheelItemHeight)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label(item),
                    style = Heading3_20m,
                    color = color,
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

@Preview(name = "SessionEdit - 시간설정 시트", showBackground = true, backgroundColor = 0xFFF3F0EB)
@Composable
private fun TimePickerSheetContentPreview() {
    GominittaTheme {
        TimePickerSheetContent(
            initial = EditableDateTime(month = 4, day = 13, hour = 9, minute = 0, amPm = "PM"),
            onConfirm = {},
        )
    }
}
