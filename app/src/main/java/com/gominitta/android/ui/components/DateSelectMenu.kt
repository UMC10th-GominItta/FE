package com.gominitta.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gominitta.android.R
import com.gominitta.android.ui.theme.Body3_14r
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.Primary200
import com.gominitta.android.ui.theme.White800
import com.gominitta.android.ui.theme.spacing

enum class DateRangeOption(
    val label: String,
    internal val textWidth: Dp,
) {
    LAST_30_DAYS("최근 30일", 56.dp),
    LAST_2_WEEKS("최근 2주", 47.dp),
    LAST_60_DAYS("최근 60일", 56.dp),
}

@Composable
fun DateSelectMenu(
    selectedOption: DateRangeOption,
    onOptionSelected: (DateRangeOption) -> Unit,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false,
) {
    var expanded by rememberSaveable { mutableStateOf(initiallyExpanded) }

    // 닫힌 버튼: 24dp 전체 모서리 / 펼친 메뉴: 디자인 시스템의 16dp large 모서리
    val shape: Shape = if (expanded) {
        MaterialTheme.shapes.large
    } else {
        RoundedCornerShape(MaterialTheme.spacing.lg)
    }

    Column(
        modifier = modifier
            .width(96.dp)
            .height(if (expanded) 96.dp else MaterialTheme.spacing.xl)
            .clip(shape),
    ) {
        // 첫 번째 버튼: 닫힌 상태에서는 단독 버튼, 펼친 상태에서는 메뉴의 상단 버튼
        DateSelectMenuItem(
            option = selectedOption,
            showArrow = true,
            arrowPointsUp = expanded,
            showBottomBorder = expanded,
            roundTopCorners = true,
            roundBottomCorners = !expanded,
            onClick = { expanded = !expanded },
        )

        if (expanded) {
            val remainingOptions = DateRangeOption.entries.filter { it != selectedOption }
            remainingOptions.forEachIndexed { index, option ->
                // 두 번째·세 번째 버튼: 선택되지 않은 나머지 기간 옵션
                DateSelectMenuItem(
                    option = option,
                    showArrow = false,
                    arrowPointsUp = false,
                    showBottomBorder = index < remainingOptions.lastIndex,
                    roundTopCorners = false,
                    roundBottomCorners = index == remainingOptions.lastIndex,
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun DateSelectMenuItem(
    option: DateRangeOption,
    showArrow: Boolean,
    arrowPointsUp: Boolean,
    showBottomBorder: Boolean,
    roundTopCorners: Boolean,
    roundBottomCorners: Boolean,
    onClick: () -> Unit,
) {
    val itemShape: Shape = when {
        roundTopCorners && roundBottomCorners -> RoundedCornerShape(MaterialTheme.spacing.lg)
        roundTopCorners -> RoundedCornerShape(
            topStart = MaterialTheme.spacing.md,
            topEnd = MaterialTheme.spacing.md,
        )
        roundBottomCorners -> RoundedCornerShape(
            bottomStart = MaterialTheme.spacing.md,
            bottomEnd = MaterialTheme.spacing.md,
        )
        else -> RectangleShape
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(MaterialTheme.spacing.xl)
            .clip(itemShape)
            .background(Primary200, itemShape)
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(MaterialTheme.spacing.xl)
                .padding(
                    start = 12.dp,
                    end = MaterialTheme.spacing.xs,
                    top = MaterialTheme.spacing.xs,
                    bottom = MaterialTheme.spacing.xs,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showArrow) {
                Box(
                    modifier = Modifier.width(56.dp),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    Text(
                        text = option.label,
                        modifier = Modifier.width(option.textWidth),
                        color = Gray800,
                        style = Body3_14r,
                        // 닫힌 버튼은 중앙 정렬, 펼친 메뉴의 상단 버튼은 오른쪽 정렬
                        textAlign = if (arrowPointsUp) TextAlign.End else TextAlign.Center,
                        maxLines = 1,
                    )
                }
            } else {
                Text(
                    text = option.label,
                    modifier = Modifier.width(option.textWidth),
                    color = Gray800,
                    style = Body3_14r,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                )
            }

            if (showArrow) {
                Box(
                    modifier = Modifier.size(MaterialTheme.spacing.lg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = if (arrowPointsUp) "기간 메뉴 닫기" else "기간 메뉴 열기",
                        modifier = Modifier
                            .size(MaterialTheme.spacing.md)
                            .rotate(if (arrowPointsUp) 90f else -90f),
                        tint = Gray800,
                    )
                }
            }
        }

        if (showBottomBorder) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(White800),
            )
        }
    }
}

@Preview(
    name = "Date select menu - collapsed",
    showBackground = true,
    backgroundColor = 0xFFFEFEFB,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun DateSelectMenuPreview() {
    GominittaTheme {
        var selectedOption by rememberSaveable { mutableStateOf(DateRangeOption.LAST_30_DAYS) }

        DateSelectMenu(
            selectedOption = selectedOption,
            onOptionSelected = { selectedOption = it },
            modifier = Modifier.padding(20.dp),
        )
    }
}
