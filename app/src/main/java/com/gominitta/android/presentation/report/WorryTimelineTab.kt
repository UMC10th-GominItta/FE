package com.gominitta.android.presentation.report

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gominitta.android.R
import com.gominitta.android.ui.components.DateRangeOption
import com.gominitta.android.ui.components.DateSelectMenu
import com.gominitta.android.ui.components.HeartReportTab
import com.gominitta.android.ui.components.HeatMap
import com.gominitta.android.ui.components.ReportCard
import com.gominitta.android.ui.theme.Body2_15r
import com.gominitta.android.ui.theme.Body3_14r
import com.gominitta.android.ui.theme.Button1_15m
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Gray200
import com.gominitta.android.ui.theme.Gray400
import com.gominitta.android.ui.theme.Gray600
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.Heading2_22m
import com.gominitta.android.ui.theme.Primary800

// =============================================================================
// 걱정 타임라인 탭 진입점
// =============================================================================

/** 걱정 타임라인 탭의 기본 카드 골격입니다. */
@Composable
internal fun WorryTimelineTab(
    modifier: Modifier = Modifier,
) {
    var selectedDateRange by rememberSaveable {
        mutableStateOf(DateRangeOption.LAST_30_DAYS)
    }
    val reportData = worryTimelineDummyData(selectedDateRange)
    val maxFrequency = reportData.frequencies.flatten().maxOrNull() ?: 0

    // -------------------------------------------------------------------------
    // 메인 리포트 카드: 335 × 461dp
    // -------------------------------------------------------------------------
    ReportCard(
        modifier = modifier,
        height = 461.dp,
    ) {
        // ---------------------------------------------------------------------
        // 카드 상단: 제목 및 설명
        // ---------------------------------------------------------------------
        Column(
            modifier = Modifier
                .offset(x = 16.dp, y = 16.dp)
                .size(width = 303.dp, height = 53.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(303.dp)
                    .height(32.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = "걱정 타임라인",
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(31.dp),
                    color = Gray800,
                    style = Heading2_22m,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                )
            }

            Text(
                text = "걱정이 자주 찾아오는 요일과 시간대에요.",
                modifier = Modifier
                    .width(303.dp)
                    .height(21.dp),
                color = Gray600,
                style = Body2_15r,
                maxLines = 1,
            )
        }

        // ---------------------------------------------------------------------
        // 카드 우측 상단: 기간 선택 메뉴
        // ---------------------------------------------------------------------
        DateSelectMenu(
            selectedOption = selectedDateRange,
            onOptionSelected = { selectedDateRange = it },
            modifier = Modifier.offset(x = 223.dp, y = 16.dp),
            initiallyExpanded = false,
        )

        Row(
            modifier = Modifier
                .offset(x = 92.dp, y = 83.dp)
                .size(width = 227.dp, height = 21.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            listOf("월", "화", "수", "목", "금", "토", "일").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.size(width = 29.dp, height = 21.dp),
                    color = Primary800,
                    style = Body2_15r,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        }

        val timeSlots = listOf(
            Triple(R.drawable.ic_morning, "아침", "06-12시"),
            Triple(R.drawable.ic_noon, "오후", "12-18시"),
            Triple(R.drawable.ic_evening, "저녁", "18-24시"),
            Triple(R.drawable.ic_night, "밤", "00-06시"),
        )

        Column(
            modifier = Modifier
                .offset(x = 16.dp, y = 106.dp)
                .size(width = 303.dp, height = 196.dp),
        ) {
            timeSlots.forEachIndexed { rowIndex, (iconRes, label, time) ->
                Row(
                    modifier = Modifier.size(width = 303.dp, height = 49.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(
                        modifier = Modifier
                            .size(width = 66.dp, height = 49.dp)
                            .drawBehind {
                                val strokeWidth = 1.dp.toPx()
                                drawLine(
                                    color = Gray200,
                                    start = Offset(0f, strokeWidth / 2f),
                                    end = Offset(size.width, strokeWidth / 2f),
                                    strokeWidth = strokeWidth,
                                )
                                drawLine(
                                    color = Gray200,
                                    start = Offset(0f, size.height - strokeWidth / 2f),
                                    end = Offset(size.width, size.height - strokeWidth / 2f),
                                    strokeWidth = strokeWidth,
                                )
                            }
                            .padding(4.dp),
                    ) {
                        Row(
                            modifier = Modifier.size(width = 58.dp, height = 21.dp),
                            horizontalArrangement = Arrangement.spacedBy(
                                space = 4.dp,
                                alignment = Alignment.CenterHorizontally,
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                text = label,
                                modifier = Modifier
                                    .offset(y = 1.dp)
                                    .size(width = 26.dp, height = 21.dp),
                                color = Gray800,
                                style = Body2_15r,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                        }

                        Text(
                            text = time,
                            modifier = Modifier.size(width = 58.dp, height = 20.dp),
                            color = Gray800,
                            style = Body3_14r,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                        )
                    }

                    HeatMap(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        frequencies = reportData.frequencies.getOrElse(rowIndex) {
                            List(7) { 0 }
                        },
                        maxFrequency = maxFrequency,
                    )
                }
            }
        }

        // ---------------------------------------------------------------------
        // 카드 하단: 점선 구분선
        // ---------------------------------------------------------------------
        Canvas(
            modifier = Modifier
                .offset(x = 16.dp, y = 326.dp)
                .size(width = 303.dp, height = 1.dp),
        ) {
            drawLine(
                color = Gray400,
                start = Offset.Zero,
                end = Offset(size.width, 0f),
                strokeWidth = 0.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(
                    intervals = floatArrayOf(4.dp.toPx(), 4.dp.toPx()),
                ),
            )
        }

        // ---------------------------------------------------------------------
        // 카드 하단: 분석 결과 및 도움말
        // ---------------------------------------------------------------------
        Column(
            modifier = Modifier
                .offset(x = 16.dp, y = 338.dp)
                .size(width = 303.dp, height = 107.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = reportData.summary,
                modifier = Modifier
                    .width(303.dp)
                    .height(63.dp),
                color = Gray800,
                style = Button1_15m,
            )

            Text(
                text = reportData.tip,
                modifier = Modifier
                    .width(303.dp)
                    .height(40.dp),
                color = Gray600,
                style = Body3_14r,
            )
        }
    }
}

// =============================================================================
// 전체 화면 Preview
// =============================================================================

@Preview(
    name = "Worry timeline - groundwork",
    showBackground = true,
    backgroundColor = 0xFFFEFEFB,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun WorryTimelineTabPreview() {
    GominittaTheme {
        ReportScreen(
            onNavigateBack = {},
            initialTab = HeartReportTab.WORRY_TIMELINE,
        )
    }
}
