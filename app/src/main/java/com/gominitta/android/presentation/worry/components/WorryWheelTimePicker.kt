package com.gominitta.android.presentation.worry.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gominitta.android.ui.theme.Body1_16m
import com.gominitta.android.ui.theme.Gray400
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.Title2_18sb
import com.gominitta.android.ui.theme.White800
import kotlin.math.abs

private val WheelRowHeight = 44.dp
private const val WheelVisibleRows = 3
private val WheelHeight = WheelRowHeight * WheelVisibleRows

/**
 * 걱정 시간 선택 휠 피커 — 월/일/시/분/오전·오후 5개 열을 스냅 스크롤로 고른다.
 * 일은 31 고정(월별 일수 보정 없음). 값이 바뀔 때마다 [onValueChange]로 전체 값을 전달한다.
 */
@Composable
fun WorryWheelTimePicker(
    initialMonth: Int,
    initialDay: Int,
    initialHour: Int,
    initialMinute: Int,
    initialIsPm: Boolean,
    onValueChange: (month: Int, day: Int, hour: Int, minute: Int, isPm: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var month by remember { mutableStateOf(initialMonth) }
    var day by remember { mutableStateOf(initialDay) }
    var hour by remember { mutableStateOf(initialHour) }
    var minute by remember { mutableStateOf(initialMinute) }
    var isPm by remember { mutableStateOf(initialIsPm) }

    LaunchedEffect(month, day, hour, minute, isPm) {
        onValueChange(month, day, hour, minute, isPm)
    }

    Box(modifier = modifier.fillMaxWidth()) {
        // 선택 행 하이라이트 — 열마다가 아니라 휠 전체 가로로 하나만.
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(WheelRowHeight)
                .clip(RoundedCornerShape(12.dp))
                .background(White800.copy(alpha = 0.5f)),
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            WorryWheelColumn(
                items = (1..12).map { "${it}월" },
                initialIndex = month - 1,
                onSettle = { month = it + 1 },
                modifier = Modifier.weight(1f),
            )
            WorryWheelColumn(
                items = (1..31).map { "${it}일" },
                initialIndex = day - 1,
                onSettle = { day = it + 1 },
                modifier = Modifier.weight(1f),
            )
            WorryWheelColumn(
                items = (1..12).map { "$it" },
                initialIndex = hour - 1,
                onSettle = { hour = it + 1 },
                modifier = Modifier.weight(1f),
            )
            WorryWheelColumn(
                items = (0..59).map { "%02d".format(it) },
                initialIndex = minute,
                onSettle = { minute = it },
                modifier = Modifier.weight(1f),
            )
            WorryWheelColumn(
                items = listOf("AM", "PM"),
                initialIndex = if (isPm) 1 else 0,
                onSettle = { isPm = it == 1 },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/** 휠 피커의 단일 열 — 스냅 스크롤 [LazyColumn], 스크롤이 멈추면 선택 인덱스를 [onSettle]로 알린다. */
@Composable
private fun WorryWheelColumn(
    items: List<String>,
    initialIndex: Int,
    onSettle: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    // 상단 contentPadding 1행 때문에, item N을 뷰포트 가운데에 두려면 firstVisible이 N-1이어야 한다.
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (initialIndex - 1).coerceAtLeast(0))
    val flingBehavior = rememberSnapFlingBehavior(listState)

    val centerIndex by remember(listState) {
        derivedStateOf {
            val info = listState.layoutInfo
            val center = (info.viewportStartOffset + info.viewportEndOffset) / 2f
            info.visibleItemsInfo.minByOrNull { abs((it.offset + it.size / 2f) - center) }?.index
                ?: initialIndex
        }
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            onSettle(centerIndex)
        }
    }

    LazyColumn(
        state = listState,
        flingBehavior = flingBehavior,
        modifier = modifier.height(WheelHeight),
        contentPadding = PaddingValues(vertical = WheelRowHeight),
    ) {
        itemsIndexed(items) { index, text ->
            val selected = index == centerIndex
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(WheelRowHeight),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = text,
                    style = if (selected) Title2_18sb else Body1_16m,
                    color = if (selected) Gray800 else Gray400,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
