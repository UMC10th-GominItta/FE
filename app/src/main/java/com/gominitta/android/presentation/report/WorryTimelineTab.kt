package com.gominitta.android.presentation.report

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gominitta.android.R
import com.gominitta.android.ui.components.DateRangeOption
import com.gominitta.android.ui.components.GominittaDateSelectMenu
import com.gominitta.android.ui.components.GominittaHeatMap
import com.gominitta.android.ui.components.GominittaReportCard
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.gray400Token
import com.gominitta.android.ui.theme.heading2Token

@Composable
internal fun WorryTimelineTab(
    selectedRange: DateRangeOption = DateRangeOption.LAST_30_DAYS,
    data: WorryTimelineReportData? = worryTimelineDummyData(selectedRange),
    onRangeSelected: (DateRangeOption) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    // 걱정 기록이 기준 개수보다 적으면 히트맵 대신 데이터 부족 안내를 표시합니다.
    if (data?.canRender == true) {
        WorryTimelineDataCard(selectedRange, onRangeSelected, data, modifier)
    } else {
        WorryTimelineEmptyCard(selectedRange, onRangeSelected, modifier)
    }
}

@Composable
private fun WorryTimelineEmptyCard(
    selectedRange: DateRangeOption,
    onRangeSelected: (DateRangeOption) -> Unit,
    modifier: Modifier,
) {
    GominittaReportCard(modifier = modifier, height = 453.dp) {
        WorryTimelineHeader(selectedRange, onRangeSelected)
        Text(
            text = "아직 리포트를 분석하기에 걱정 기록이 조금 부족해요.\n세션을 조금 더 진행해 볼까요?",
            modifier = Modifier.offset(x = 50.dp, y = 190.dp).width(235.dp),
            color = MaterialTheme.colorScheme.gray400Token,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun WorryTimelineDataCard(
    selectedRange: DateRangeOption,
    onRangeSelected: (DateRangeOption) -> Unit,
    data: WorryTimelineReportData,
    modifier: Modifier,
) {
    val rowDividerColor = MaterialTheme.colorScheme.outlineVariant
    val sectionDividerColor = MaterialTheme.colorScheme.gray400Token
    val timeSlotIcons = listOf(
        R.drawable.ic_morning,
        R.drawable.ic_noon,
        R.drawable.ic_evening,
        R.drawable.ic_night,
    )

    GominittaReportCard(modifier = modifier, height = null, minHeight = 461.dp) {
        WorryTimelineHeader(selectedRange, onRangeSelected)

        // 히트맵 열의 기준이 되는 월요일부터 일요일까지의 요일을 표시합니다.
        Row(
            modifier = Modifier.offset(x = 92.dp, y = 83.dp).size(227.dp, 21.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            listOf("월", "화", "수", "목", "금", "토", "일").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.size(29.dp, 21.dp),
                    color = MaterialTheme.colorScheme.onSecondary,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        }

        Column(
            modifier = Modifier.offset(x = 16.dp, y = 106.dp).size(303.dp, 196.dp),
        ) {
            // 아침·오후·저녁·밤 순서로 시간대 정보와 히트맵 한 행을 구성합니다.
            timelineTimeSlots.forEachIndexed { rowIndex, slot ->
                Row(
                    modifier = Modifier.size(303.dp, 49.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(
                        modifier = Modifier
                            .size(66.dp, 49.dp)
                            .drawBehind {
                                val stroke = 1.dp.toPx()
                                drawLine(
                                    rowDividerColor,
                                    Offset(0f, stroke / 2),
                                    Offset(size.width, stroke / 2),
                                    stroke,
                                )
                                drawLine(
                                    rowDividerColor,
                                    Offset(0f, size.height - stroke / 2),
                                    Offset(size.width, size.height - stroke / 2),
                                    stroke,
                                )
                            }
                            .padding(4.dp),
                    ) {
                        Row(
                            modifier = Modifier.size(58.dp, 21.dp),
                            horizontalArrangement = Arrangement.spacedBy(
                                4.dp,
                                Alignment.CenterHorizontally,
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(timeSlotIcons[rowIndex]),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                text = slot.label,
                                modifier = Modifier.size(26.dp, 21.dp),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                        }
                        Text(
                            text = slot.range,
                            modifier = Modifier.size(58.dp, 20.dp),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                        )
                    }

                    GominittaHeatMap(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        // 누락된 행은 7일 모두 기록이 없는 상태로 안전하게 표시합니다.
                        levels = data.levels.getOrElse(rowIndex) { List(7) { 0 } },
                    )
                }
            }
        }

        Canvas(
            modifier = Modifier.offset(x = 16.dp, y = 326.dp).size(303.dp, 1.dp),
        ) {
            drawLine(
                color = sectionDividerColor,
                start = Offset.Zero,
                end = Offset(size.width, 0f),
                strokeWidth = 0.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(
                    floatArrayOf(4.dp.toPx(), 4.dp.toPx()),
                ),
            )
        }

        Column(
            modifier = Modifier
                .padding(start = 16.dp, top = 338.dp, end = 16.dp, bottom = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            // 분석 문구와 팁의 길이에 맞춰 카드 하단 높이가 확장됩니다.
            Text(
                text = data.feedbackText(),
                modifier = Modifier.width(303.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                text = TIMELINE_TIP,
                modifier = Modifier.width(303.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun BoxScope.WorryTimelineHeader(
    selectedRange: DateRangeOption,
    onRangeSelected: (DateRangeOption) -> Unit,
) {
    Column(
        modifier = Modifier.offset(x = 16.dp, y = 16.dp).size(303.dp, 53.dp),
    ) {
        Box(
            modifier = Modifier.size(303.dp, 32.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = "걱정 타임라인",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.heading2Token,
                maxLines = 1,
            )
        }
        Text(
            text = "걱정이 자주 찾아오는 요일과 시간대에요.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
        )
    }
    GominittaDateSelectMenu(
        selectedOption = selectedRange,
        onOptionSelected = onRangeSelected,
        modifier = Modifier.offset(x = 223.dp, y = 16.dp),
    )
}

@Preview(showBackground = true, widthDp = 375, heightDp = 535)
@Composable
private fun WorryTimelinePreview() {
    GominittaTheme { WorryTimelineTab() }
}
