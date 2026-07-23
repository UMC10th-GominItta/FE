package com.gominitta.android.presentation.mypage.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gominitta.android.presentation.mypage.model.TimeValue
import com.gominitta.android.ui.theme.Heading3_20m   // ✅ 수정: 20px Medium 스타일 import
import kotlin.math.abs
import kotlinx.coroutines.launch

private val ITEM_HEIGHT = 45.dp
private const val VISIBLE_ITEM_COUNT = 3

// 하나로 묶인 하이라이트 바의 폭 = 열 3개(58dp) + 열 사이 간격 2개(15dp).
private val WHEEL_COLUMN_WIDTH = 44.dp
private val WHEEL_COLUMN_SPACING = 15.dp   // ✅ 수정: 12.dp → 15.dp (요청 1-5 간격 15px)
private val HIGHLIGHT_WIDTH = WHEEL_COLUMN_WIDTH * 3 + WHEEL_COLUMN_SPACING * 2
private val HIGHLIGHT_CORNER_RADIUS = 12.dp

private const val INFINITE_REPEAT_COUNT = 400

private val HOURS = (1..12).toList()
private val MINUTES = (0 until 60 step 5).toList()

@Composable
fun MyPageTimePicker(
    value: TimeValue,
    onValueChange: (TimeValue) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        // 시/분/AM-PM 세 열을 하나로 묶는 단일 하이라이트 배경.
        Box(
            modifier = Modifier
                .width(HIGHLIGHT_WIDTH)
                .height(ITEM_HEIGHT)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), // ✅ 수정: White/800_50 (#FEFEFB 50%)
                    shape = RoundedCornerShape(HIGHLIGHT_CORNER_RADIUS),
                ),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(WHEEL_COLUMN_SPACING),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WheelPicker(
                items = HOURS,
                selectedItem = value.hour,
                label = { it.toString().padStart(2, '0') },
                onSelectedItemChange = { onValueChange(value.copy(hour = it)) },
                infiniteScroll = true,
            )

            WheelPicker(
                items = MINUTES,
                selectedItem = value.minute,
                label = { it.toString().padStart(2, '0') },
                onSelectedItemChange = { onValueChange(value.copy(minute = it)) },
                infiniteScroll = true,
            )

            WheelPicker(
                items = listOf(false, true),
                selectedItem = value.isPm,
                label = { isPm -> if (isPm) "PM" else "AM" },
                onSelectedItemChange = { onValueChange(value.copy(isPm = it)) },
                infiniteScroll = false,
            )
        }
    }
}

@Composable
private fun <T> WheelPicker(
    items: List<T>,
    selectedItem: T,
    label: (T) -> String,
    onSelectedItemChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    infiniteScroll: Boolean = true,
) {
    val itemCount = items.size
    val repeatCount = if (infiniteScroll) INFINITE_REPEAT_COUNT else 1
    val virtualCount = itemCount * repeatCount
    val middleBlock = repeatCount / 2

    fun virtualIndexOf(item: T): Int {
        val localIndex = items.indexOf(item).coerceAtLeast(0)
        return middleBlock * itemCount + localIndex
    }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = virtualIndexOf(selectedItem),
    )
    val coroutineScope = rememberCoroutineScope()

    val centeredVirtualIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val viewportCenter =
                (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
            layoutInfo.visibleItemsInfo
                .minByOrNull { info -> abs((info.offset + info.size / 2) - viewportCenter) }
                ?.index
                ?: listState.firstVisibleItemIndex
        }
    }

    LaunchedEffect(selectedItem, itemCount) {
        if (!listState.isScrollInProgress) {
            val target = virtualIndexOf(selectedItem)
            val current = ((centeredVirtualIndex % itemCount) + itemCount) % itemCount
            val selectedLocalIndex = items.indexOf(selectedItem).coerceAtLeast(0)
            if (current != selectedLocalIndex) {
                listState.scrollToItem(target)
            }
        }
    }

    LaunchedEffect(listState, itemCount) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { scrolling ->
                if (!scrolling) {
                    val localIndex = ((centeredVirtualIndex % itemCount) + itemCount) % itemCount
                    val item = items[localIndex]
                    if (item != selectedItem) {
                        onSelectedItemChange(item)
                    }
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .width(WHEEL_COLUMN_WIDTH)
            .height(ITEM_HEIGHT * VISIBLE_ITEM_COUNT),
        flingBehavior = rememberSnapFlingBehavior(listState),
        contentPadding = PaddingValues(
            vertical = ITEM_HEIGHT * (VISIBLE_ITEM_COUNT / 2),
        ),
    ) {
        items(count = virtualCount) { index ->
            val localIndex = index % itemCount
            val isSelected = index == centeredVirtualIndex

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ITEM_HEIGHT)
                    .clickable {
                        coroutineScope.launch {
                            listState.animateScrollToItem(index)
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label(items[localIndex]),
                    // ✅ 수정: 선택된 값은 Heading3_20m(Pretendard Medium 20px), 나머지는 기존 bodySmall 유지
                    style = if (isSelected) Heading3_20m else MaterialTheme.typography.bodySmall,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                    },
                )
            }
        }
    }
}