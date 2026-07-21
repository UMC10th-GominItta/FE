package com.gominitta.android.presentation.report

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gominitta.android.ui.components.DateRangeOption
import com.gominitta.android.ui.components.DateSelectMenu
import com.gominitta.android.ui.components.HeartReportTab
import com.gominitta.android.ui.components.ReportCard
import com.gominitta.android.ui.theme.Body2_15r
import com.gominitta.android.ui.theme.Body3_14r
import com.gominitta.android.ui.theme.Body1_16m
import com.gominitta.android.ui.theme.AccentCream100
import com.gominitta.android.ui.theme.Button1_15m
import com.gominitta.android.ui.theme.Gray600
import com.gominitta.android.ui.theme.Gray400
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Heading2_22m
import com.gominitta.android.ui.theme.Heading1_24sb
import com.gominitta.android.ui.theme.Primary200
import com.gominitta.android.ui.theme.Primary300
import com.gominitta.android.ui.theme.Primary800
import com.gominitta.android.ui.theme.spacing
import androidx.compose.material3.MaterialTheme

/**
 * 불안 온도차 탭의 데이터 없음 카드 밑작업입니다.
 * 세부 문구와 콘텐츠는 추후 전달될 디자인에 맞춰 변경합니다.
 */
@Composable
internal fun AnxietyTemperatureTab(
    modifier: Modifier = Modifier,
    cardIndices: List<Int> = listOf(0, 1, 2),
) {
    var selectedDateRange by rememberSaveable {
        mutableStateOf(DateRangeOption.LAST_30_DAYS)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 28.dp)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
    ) {
        cardIndices.forEach { cardIndex ->
            ReportCard(height = 479.dp) {
                val isSecondCard = cardIndex == 1
                val isThirdCard = cardIndex == 2

        // 임시 공통 상단 제목 및 설명 영역
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
                    text = "불안 온도차",
                    modifier = Modifier
                        .width(115.dp)
                        .height(31.dp),
                    color = Gray800,
                    style = Heading2_22m,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                )
            }

            Text(
                text = "걱정 예약 시와 마음 세션 후 변화에요.",
                modifier = Modifier
                    .width(303.dp)
                    .height(21.dp),
                color = Gray600,
                style = Body2_15r,
                maxLines = 1,
            )
        }

        // 카드 우측 상단 기간 선택 메뉴
                DateSelectMenu(
                    selectedOption = selectedDateRange,
                    onOptionSelected = { selectedDateRange = it },
                    modifier = Modifier.offset(x = 223.dp, y = 16.dp),
                    initiallyExpanded = false,
                )

                Row(
                        modifier = Modifier
                            .offset(x = 16.dp, y = 85.dp)
                            .size(width = 303.dp, height = 86.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            modifier = Modifier
                                .size(width = 150.dp, height = 86.dp)
                                .background(
                                    color = when {
                                        isThirdCard -> AccentCream100
                                        isSecondCard -> Primary200
                                        else -> Primary300
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                )
                                .padding(start = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Primary800),
                            )

                            Column(
                                modifier = Modifier.size(width = 46.dp, height = 54.dp),
                            ) {
                                Text(
                                    text = "예약 시",
                                    modifier = Modifier.size(width = 46.dp, height = 20.dp),
                                    color = Gray800,
                                    style = Body3_14r,
                                    maxLines = 1,
                                )

                                Row(
                                    modifier = Modifier.size(width = 46.dp, height = 34.dp),
                                ) {
                                    Text(
                                        text = when {
                                            isThirdCard -> "6"
                                            isSecondCard -> "4"
                                            else -> "8"
                                        },
                                        modifier = Modifier.size(width = 16.dp, height = 34.dp),
                                        color = Gray800,
                                        style = Heading1_24sb,
                                        maxLines = 1,
                                    )
                                    Text(
                                        text = "/ 10",
                                        modifier = Modifier
                                            .offset(y = 6.dp)
                                            .size(width = 30.dp, height = 22.dp),
                                        color = Gray800,
                                        style = Body1_16m,
                                        maxLines = 1,
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .size(width = 150.dp, height = 86.dp)
                                .background(
                                    color = when {
                                        isThirdCard -> AccentCream100
                                        isSecondCard -> Primary300
                                        else -> Primary200
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                )
                                .padding(start = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Primary800),
                            )

                            Column(
                                modifier = Modifier.size(width = 46.dp, height = 54.dp),
                            ) {
                                Text(
                                    text = "세션 후",
                                    modifier = Modifier.size(width = 46.dp, height = 20.dp),
                                    color = Gray800,
                                    style = Body3_14r,
                                    maxLines = 1,
                                )

                                Row(
                                    modifier = Modifier.size(width = 46.dp, height = 34.dp),
                                ) {
                                    Text(
                                        text = when {
                                            isThirdCard -> "6"
                                            isSecondCard -> "8"
                                            else -> "4"
                                        },
                                        modifier = Modifier.size(width = 16.dp, height = 34.dp),
                                        color = Gray800,
                                        style = Heading1_24sb,
                                        maxLines = 1,
                                    )
                                    Text(
                                        text = "/ 10",
                                        modifier = Modifier
                                            .offset(y = 6.dp)
                                            .size(width = 30.dp, height = 22.dp),
                                        color = Gray800,
                                        style = Body1_16m,
                                        maxLines = 1,
                                    )
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .offset(x = 20.dp, y = 191.dp)
                            .size(width = 15.dp, height = 160.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        listOf("10", "8", "6", "4", "2", "0").forEach { value ->
                            Text(
                                text = value,
                                modifier = Modifier.size(width = 15.dp, height = 20.dp),
                                color = Gray800,
                                style = Body3_14r,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                        }
                    }

                    Canvas(
                        modifier = Modifier
                            .offset(
                                x = if (isThirdCard) 65.28.dp else 69.dp,
                                y = if (isThirdCard) 254.dp else 226.dp,
                            )
                            .size(
                                width = if (isThirdCard) 220.44.dp else 213.dp,
                                height = if (isThirdCard) 6.dp else 62.dp,
                            ),
                    ) {
                        val startPoint = Offset(
                            x = 3.dp.toPx(),
                            y = when {
                                isThirdCard -> 3.dp.toPx()
                                isSecondCard -> 59.dp.toPx()
                                else -> 3.dp.toPx()
                            },
                        )
                        val endPoint = Offset(
                            x = if (isThirdCard) 217.44.dp.toPx() else 210.dp.toPx(),
                            y = when {
                                isThirdCard -> 3.dp.toPx()
                                isSecondCard -> 3.dp.toPx()
                                else -> 59.dp.toPx()
                            },
                        )

                        drawLine(
                            color = Gray800,
                            start = startPoint,
                            end = endPoint,
                            strokeWidth = 1.dp.toPx(),
                        )
                        drawCircle(
                            color = Gray800,
                            radius = 3.dp.toPx(),
                            center = startPoint,
                        )
                        drawCircle(
                            color = Gray800,
                            radius = 3.dp.toPx(),
                            center = endPoint,
                        )
                    }

                // 세 카드가 공통으로 사용하는 하단 점선 구분선
                Canvas(
                    modifier = Modifier
                        .offset(x = 16.dp, y = 366.dp)
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

                if (cardIndex == 0) {
                    // 1번 카드 하단 분석 요약 및 도움말
                    Column(
                        modifier = Modifier
                            .offset(x = 16.dp, y = 378.dp)
                            .size(width = 303.dp, height = 85.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "걱정을 마주하고 마음이 한결 가벼워졌어요.",
                            modifier = Modifier
                                .width(303.dp)
                                .height(21.dp),
                            color = Gray800,
                            style = Button1_15m,
                            maxLines = 1,
                        )

                        Text(
                            text = "tip. 기록을 돌아보면, 걱정을 마주한 뒤 감정이 차분해지는 패턴이 보여요. 이 흐름을 기억하며, 앞으로도 나를 믿어보세요.",
                            modifier = Modifier
                                .width(303.dp)
                                .height(60.dp),
                            color = Gray600,
                            style = Body3_14r,
                        )
                    }
                }

                if (cardIndex == 1) {
                    // 2번 카드 하단 분석 요약 및 도움말
                    Column(
                        modifier = Modifier
                            .offset(x = 16.dp, y = 378.dp)
                            .size(width = 303.dp, height = 85.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "아직은 마음에 복잡한 생각들이 남아있네요.",
                            modifier = Modifier
                                .width(303.dp)
                                .height(21.dp),
                            color = Gray800,
                            style = Button1_15m,
                            maxLines = 1,
                        )

                        Text(
                            text = "tip. 원인을 완벽하게 없애지 못했어도, 내 마음을 들여다본 것만으로도 큰 시작이에요. 지금 나에게 가장 필요한 '마음 레시피'를 찾고, 실천하며 잠시 쉬어가 보세요.",
                            modifier = Modifier
                                .width(303.dp)
                                .height(60.dp),
                            color = Gray600,
                            style = Body3_14r,
                        )
                    }
                }

                if (cardIndex == 2) {
                    // 3번 카드 하단 분석 요약 및 도움말
                    Column(
                        modifier = Modifier
                            .offset(x = 16.dp, y = 378.dp)
                            .size(width = 303.dp, height = 85.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = "아직은 마음에 복잡한 생각들이 남아있네요.",
                            modifier = Modifier
                                .width(303.dp)
                                .height(21.dp),
                            color = Gray800,
                            style = Button1_15m,
                            maxLines = 1,
                        )

                        Text(
                            text = "tip. 원인을 완벽하게 없애지 못했어도, 내 마음을 들여다본 것만으로도 큰 시작이에요. 지금 나에게 가장 필요한 '마음 레시피'를 찾고, 실천하며 잠시 쉬어가 보세요.",
                            modifier = Modifier
                                .width(303.dp)
                                .height(60.dp),
                            color = Gray600,
                            style = Body3_14r,
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    name = "Anxiety temperature - groundwork",
    showBackground = true,
    backgroundColor = 0xFFFEFEFB,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun AnxietyTemperatureTabPreview() {
    GominittaTheme {
        ReportScreen(
            onNavigateBack = {},
            initialTab = HeartReportTab.ANXIETY_TEMPERATURE,
        ) { selectedTab ->
            if (selectedTab == HeartReportTab.ANXIETY_TEMPERATURE) {
                AnxietyTemperatureTab()
            }
        }
    }
}

@Preview(
    name = "Anxiety temperature - card 1",
    showBackground = true,
    backgroundColor = 0xFFFEFEFB,
    widthDp = 375,
    heightDp = 535,
)
@Composable
private fun AnxietyTemperatureCard1Preview() {
    GominittaTheme {
        AnxietyTemperatureTab(cardIndices = listOf(0))
    }
}

@Preview(
    name = "Anxiety temperature - card 2",
    showBackground = true,
    backgroundColor = 0xFFFEFEFB,
    widthDp = 375,
    heightDp = 535,
)
@Composable
private fun AnxietyTemperatureCard2Preview() {
    GominittaTheme {
        AnxietyTemperatureTab(cardIndices = listOf(1))
    }
}

@Preview(
    name = "Anxiety temperature - card 3",
    showBackground = true,
    backgroundColor = 0xFFFEFEFB,
    widthDp = 375,
    heightDp = 535,
)
@Composable
private fun AnxietyTemperatureCard3Preview() {
    GominittaTheme {
        AnxietyTemperatureTab(cardIndices = listOf(2))
    }
}
