package com.gominitta.android.presentation.report

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import com.gominitta.android.ui.components.GominittaDateSelectMenu
import com.gominitta.android.ui.components.HeartReportTab
import com.gominitta.android.ui.components.GominittaReportCard
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.gray400Token
import com.gominitta.android.ui.theme.heading2Token
import com.gominitta.android.ui.theme.primary300Token
import com.gominitta.android.ui.theme.spacing
import androidx.compose.material3.MaterialTheme

/**
 * 걱정 예약 시점과 마음 세션 완료 후의 불안 점수를 비교하는 카드입니다.
 *
 * 실제 화면에서는 카드 한 장만 노출합니다. [cardIndices]는 감소·증가·동일 상태를
 * 개별 Preview에서 확인하기 위해 남겨둔 테스트용 진입점입니다.
 * 기간을 바꾸면 [anxietyDummyData]가 점수와 피드백을 제공하고, 블록 색상과 그래프
 * 꼭짓점은 해당 점수를 기준으로 다시 계산됩니다.
 */
@Composable
internal fun AnxietyTemperatureTab(
    modifier: Modifier = Modifier,
    cardIndices: List<Int> = listOf(0),
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            space = MaterialTheme.spacing.md,
            alignment = Alignment.CenterHorizontally,
        ),
    ) {
        cardIndices.forEach { cardIndex ->
            // Preview 카드끼리 기간 선택 상태가 공유되지 않도록 카드별로 저장합니다.
            var selectedDateRange by rememberSaveable(cardIndex) {
                mutableStateOf(DateRangeOption.LAST_30_DAYS)
            }

            GominittaReportCard(height = 479.dp) {
                // 선택 기간과 카드 상태에 대응하는 점수·피드백을 한 객체에서 읽습니다.
                val reportData = anxietyDummyData(selectedDateRange, cardIndex)
                val isRising = reportData.afterScore > reportData.beforeScore
                val isFlat = reportData.afterScore == reportData.beforeScore
                val graphColor = MaterialTheme.colorScheme.onSurface
                val dividerColor = MaterialTheme.colorScheme.gray400Token

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
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.heading2Token,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                )
            }

            Text(
                text = "걱정 예약 시와 마음 세션 후 변화에요.",
                modifier = Modifier
                    .width(303.dp)
                    .height(21.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
            )
        }

        // 카드 우측 상단 기간 선택 메뉴
                GominittaDateSelectMenu(
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
                                        isFlat -> MaterialTheme.colorScheme.tertiary
                                        isRising -> MaterialTheme.colorScheme.secondaryContainer
                                        else -> MaterialTheme.colorScheme.primary300Token
                                    },
                                    shape = MaterialTheme.shapes.large,
                                )
                                .padding(start = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(MaterialTheme.colorScheme.onSecondary),
                            )

                            Column(
                                modifier = Modifier.size(width = 46.dp, height = 54.dp),
                            ) {
                                Text(
                                    text = "예약 시",
                                    modifier = Modifier.size(width = 46.dp, height = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                )

                                Row(
                                    modifier = Modifier.size(width = 46.dp, height = 34.dp),
                                ) {
                                    Text(
                                        text = reportData.beforeScore.toString(),
                                        modifier = Modifier.size(width = 16.dp, height = 34.dp),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.headlineLarge,
                                        maxLines = 1,
                                    )
                                    Text(
                                        text = "/ 10",
                                        modifier = Modifier
                                            .offset(y = 6.dp)
                                            .size(width = 30.dp, height = 22.dp),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.titleMedium,
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
                                        isFlat -> MaterialTheme.colorScheme.tertiary
                                        isRising -> MaterialTheme.colorScheme.primary300Token
                                        else -> MaterialTheme.colorScheme.secondaryContainer
                                    },
                                    shape = MaterialTheme.shapes.large,
                                )
                                .padding(start = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(MaterialTheme.colorScheme.onSecondary),
                            )

                            Column(
                                modifier = Modifier.size(width = 46.dp, height = 54.dp),
                            ) {
                                Text(
                                    text = "세션 후",
                                    modifier = Modifier.size(width = 46.dp, height = 20.dp),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                )

                                Row(
                                    modifier = Modifier.size(width = 46.dp, height = 34.dp),
                                ) {
                                    Text(
                                        text = reportData.afterScore.toString(),
                                        modifier = Modifier.size(width = 16.dp, height = 34.dp),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.headlineLarge,
                                        maxLines = 1,
                                    )
                                    Text(
                                        text = "/ 10",
                                        modifier = Modifier
                                            .offset(y = 6.dp)
                                            .size(width = 30.dp, height = 22.dp),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.titleMedium,
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
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                            )
                        }
                    }

                    // 0~10점 축을 기준으로 두 점의 Y 좌표를 계산해 꺾은선을 그립니다.
                    Canvas(
                        modifier = Modifier
                            .offset(x = 69.dp, y = 188.dp)
                            .size(width = 213.dp, height = 156.dp),
                    ) {
                        val startPoint = Offset(
                            x = 3.dp.toPx(),
                            y = (153 - 14 * reportData.beforeScore).dp.toPx(),
                        )
                        val endPoint = Offset(
                            x = 210.dp.toPx(),
                            y = (153 - 14 * reportData.afterScore).dp.toPx(),
                        )

                        drawLine(
                            color = graphColor,
                            start = startPoint,
                            end = endPoint,
                            strokeWidth = 1.dp.toPx(),
                        )
                        drawCircle(
                            color = graphColor,
                            radius = 3.dp.toPx(),
                            center = startPoint,
                        )
                        drawCircle(
                            color = graphColor,
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
                        color = dividerColor,
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
                            text = reportData.summary,
                            modifier = Modifier
                                .width(303.dp)
                                .height(21.dp),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                        )

                        Text(
                            text = reportData.tip,
                            modifier = Modifier
                                .width(303.dp)
                                .height(60.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
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
                            text = reportData.summary,
                            modifier = Modifier
                                .width(303.dp)
                                .height(21.dp),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                        )

                        Text(
                            text = reportData.tip,
                            modifier = Modifier
                                .width(303.dp)
                                .height(60.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
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
                            text = reportData.summary,
                            modifier = Modifier
                                .width(303.dp)
                                .height(21.dp),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                        )

                        Text(
                            text = reportData.tip,
                            modifier = Modifier
                                .width(303.dp)
                                .height(60.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
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
        )
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
