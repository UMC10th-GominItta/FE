package com.gominitta.android.presentation.worry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gominitta.android.R
import com.gominitta.android.presentation.worry.components.WorryFavoriteTime
import com.gominitta.android.presentation.worry.components.WorryFavoriteTimeCard
import com.gominitta.android.presentation.worry.components.WorryPrimaryButton
import com.gominitta.android.presentation.worry.components.WorryTimeCard
import com.gominitta.android.presentation.worry.components.WorryTimeCardState
import com.gominitta.android.presentation.worry.components.WorryTopBar
import com.gominitta.android.presentation.worry.components.WorryWheelTimePicker
import com.gominitta.android.ui.components.GominittaBackground
import com.gominitta.android.ui.theme.AccentCream200
import com.gominitta.android.ui.theme.Body1_16m
import com.gominitta.android.ui.theme.Body2_15r
import com.gominitta.android.ui.theme.Gray400
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Heading4_18m
import com.gominitta.android.ui.theme.Primary200
import com.gominitta.android.ui.theme.Primary800
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import kotlinx.coroutines.launch

private val ScrimColor = Color(0xFFFEFDF8)
private val SheetHandleColor = Color(0xFF121211)

private val SampleFavoriteTimes = listOf(
    WorryFavoriteTime("자기 전 생각 타임", 23 * 60, 24 * 60),
    WorryFavoriteTime("저녁 먹고 고민하기", 20 * 60, 21 * 60),
    WorryFavoriteTime("쉬는 시간에 잠깐", 11 * 60 + 30, 12 * 60),
    WorryFavoriteTime("쉬는 시간에 잠깐", 11 * 60 + 30, 12 * 60),
)

/** 시간을 편집 중인 카드 — 시작/종료. */
private enum class WorryTimeSlot { START, END }

/**
 * 걱정 시간 예약 (B102) — 걱정 시간 시작/종료를 휠 피커로 골라 예약한다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorryScheduleScreen(
    onNavigateNext: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val year = remember { LocalDate.now().year }
    val defaultStartTime = remember(year) { LocalDateTime.of(year, 4, 13, 21, 0) }
    val defaultEndTime = remember(year) { LocalDateTime.of(year, 4, 13, 22, 0) }

    var startTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var endTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var activeSlot by remember { mutableStateOf<WorryTimeSlot?>(null) }
    var selectedFavoriteIndex by remember { mutableStateOf<Int?>(null) }

    val displayStartTime = startTime ?: defaultStartTime
    val displayEndTime = endTime ?: defaultEndTime

    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    val nextEnabled = run {
        val start = startTime
        val end = endTime
        start != null && end != null && start < end
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                WorryTopBar(title = "걱정 예약하기", onBack = onNavigateBack)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
                ) {
                    Spacer(Modifier.height(36.dp))

                    Text(
                        text = "지금 말고, 이 시간에 생각하기로 해요.",
                        style = Heading4_18m,
                        color = Gray800,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        WorryTimeCard(
                            dateTime = displayStartTime,
                            suffix = "부터",
                            state = cardState(activeSlot, WorryTimeSlot.START, startTime),
                            onClick = { activeSlot = WorryTimeSlot.START },
                            modifier = Modifier.weight(1f),
                        )
                        WorryTimeCard(
                            dateTime = displayEndTime,
                            suffix = "까지",
                            state = cardState(activeSlot, WorryTimeSlot.END, endTime),
                            onClick = { activeSlot = WorryTimeSlot.END },
                            modifier = Modifier.weight(1f),
                        )
                    }

                    Spacer(Modifier.height(28.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(AccentCream200),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_clock),
                                contentDescription = null,
                                tint = Primary800,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(text = "즐겨찾는 시간", style = Body1_16m, color = Gray800)
                    }

                    if (SampleFavoriteTimes.isEmpty()) {
                        Spacer(Modifier.height(28.dp))

                        Text(
                            text = "즐겨찾는 시간이 없습니다.",
                            style = Body2_15r,
                            color = Gray400,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                    } else {
                        Spacer(Modifier.height(16.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SampleFavoriteTimes.forEachIndexed { index, favorite ->
                                WorryFavoriteTimeCard(
                                    favorite = favorite,
                                    selected = selectedFavoriteIndex == index,
                                    onClick = {
                                        selectedFavoriteIndex = index
                                        val base = LocalDate.now().atStartOfDay()
                                        startTime = base.plusMinutes(favorite.startMinutes.toLong())
                                        endTime = base.plusMinutes(favorite.endMinutes.toLong())
                                    },
                                )
                            }
                        }
                    }
                }

                WorryPrimaryButton(
                    text = "다음",
                    onClick = onNavigateNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 28.dp),
                    enabled = nextEnabled,
                )
            }
        }

        if (activeSlot != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(ScrimColor.copy(alpha = 0.3f), ScrimColor),
                        ),
                    ),
            )
        }
    }

    val slot = activeSlot
    if (slot != null) {
        val current = if (slot == WorryTimeSlot.START) displayStartTime else displayEndTime

        ModalBottomSheet(
            onDismissRequest = { activeSlot = null },
            sheetState = sheetState,
            containerColor = Primary200,
            scrimColor = Color.Transparent,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            dragHandle = null,
        ) {
            var pickerMonth by remember { mutableStateOf(current.monthValue) }
            var pickerDay by remember { mutableStateOf(current.dayOfMonth) }
            var pickerHour by remember { mutableStateOf(to12Hour(current.hour)) }
            var pickerMinute by remember { mutableStateOf(current.minute) }
            var pickerIsPm by remember { mutableStateOf(current.hour >= 12) }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .size(width = 48.dp, height = 3.dp)
                        .clip(RoundedCornerShape(50))
                        .background(SheetHandleColor),
                )

                Spacer(Modifier.height(36.dp))

                WorryWheelTimePicker(
                    initialMonth = pickerMonth,
                    initialDay = pickerDay,
                    initialHour = pickerHour,
                    initialMinute = pickerMinute,
                    initialIsPm = pickerIsPm,
                    onValueChange = { month, day, hour, minute, isPm ->
                        pickerMonth = month
                        pickerDay = day
                        pickerHour = hour
                        pickerMinute = minute
                        pickerIsPm = isPm
                    },
                )

                Spacer(Modifier.height(56.dp))

                WorryPrimaryButton(
                    text = "설정하기",
                    onClick = {
                        val hour24 = to24Hour(pickerHour, pickerIsPm)
                        val safeDay = pickerDay.coerceAtMost(YearMonth.of(year, pickerMonth).lengthOfMonth())
                        val result = LocalDateTime.of(year, pickerMonth, safeDay, hour24, pickerMinute)
                        if (slot == WorryTimeSlot.START) startTime = result else endTime = result
                        selectedFavoriteIndex = null

                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) activeSlot = null
                        }
                    },
                    enabled = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 28.dp)
                        .navigationBarsPadding(),
                )
            }
        }
    }
}

// ---- 카드 상태 ----------------------------------------------------------------

/** 카드 상태 도출 — Editing > Set > Unset 우선순위. */
private fun cardState(activeSlot: WorryTimeSlot?, slot: WorryTimeSlot, time: LocalDateTime?): WorryTimeCardState = when {
    activeSlot == slot -> WorryTimeCardState.Editing
    time != null -> WorryTimeCardState.Set
    else -> WorryTimeCardState.Unset
}

// ---- 시/분 변환 --------------------------------------------------------------

private fun to12Hour(hour24: Int): Int = when {
    hour24 == 0 -> 12
    hour24 > 12 -> hour24 - 12
    else -> hour24
}

private fun to24Hour(hour12: Int, isPm: Boolean): Int = when {
    hour12 == 12 -> if (isPm) 12 else 0
    isPm -> hour12 + 12
    else -> hour12
}

// ---- Preview ---------------------------------------------------------------

@Preview(name = "WorrySchedule", showBackground = true, backgroundColor = 0xFFFEFEFB, widthDp = 375, heightDp = 812)
@Composable
private fun WorryScheduleScreenPreview() {
    GominittaTheme {
        GominittaBackground {
            WorryScheduleScreen(onNavigateNext = {}, onNavigateBack = {})
        }
    }
}
