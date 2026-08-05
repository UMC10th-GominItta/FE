package com.gominitta.android.presentation.report

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gominitta.android.R
import com.gominitta.android.ui.components.DateRangeOption
import com.gominitta.android.ui.components.GominittaDateSelectMenu
import com.gominitta.android.ui.components.GominittaReportCard
import com.gominitta.android.ui.theme.gray400Token
import com.gominitta.android.ui.theme.heading2Token
import com.gominitta.android.ui.theme.primary300Token
import com.gominitta.android.ui.theme.White800
import kotlin.math.roundToInt

@Composable
internal fun AnxietyTemperatureTab(
    selectedRange: DateRangeOption = DateRangeOption.LAST_30_DAYS,
    data: AnxietyReportData? = anxietyDummyData(selectedRange),
    onRangeSelected: (DateRangeOption) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    // 집계 표본이 없으면 그래프 대신 데이터 부족 안내를 표시합니다.
    if (data?.canRender == true) {
        AnxietyDataCard(selectedRange, onRangeSelected, data, modifier)
    } else {
        AnxietyEmptyCard(selectedRange, onRangeSelected, modifier)
    }
}

@Composable
private fun AnxietyEmptyCard(
    selectedRange: DateRangeOption,
    onRangeSelected: (DateRangeOption) -> Unit,
    modifier: Modifier,
) {
    GominittaReportCard(modifier = modifier, height = 453.dp) {
        AnxietyCardHeader(selectedRange, onRangeSelected)
        Text(
            text = stringResource(R.string.report_empty_message),
            modifier = Modifier.offset(x = 50.dp, y = 190.dp).width(235.dp),
            color = MaterialTheme.colorScheme.gray400Token,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun AnxietyDataCard(
    selectedRange: DateRangeOption,
    onRangeSelected: (DateRangeOption) -> Unit,
    data: AnxietyReportData,
    modifier: Modifier,
) {
    // 점수 변화 방향에 따라 두 점수 카드의 강조 색상을 결정합니다.
    val isRising = data.state == AnxietyChangeState.INCREASED
    val isFlat = data.state == AnxietyChangeState.MAINTAINED
    val graphColor = MaterialTheme.colorScheme.onSurface
    val dividerColor = MaterialTheme.colorScheme.gray400Token

    GominittaReportCard(modifier = modifier, height = null, minHeight = 479.dp) {
        AnxietyCardHeader(selectedRange, onRangeSelected)

        Row(
            modifier = Modifier.offset(x = 16.dp, y = 85.dp).size(303.dp, 86.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // 예약 시와 세션 후 점수를 각각 카드와 고양이 일러스트로 표시합니다.
            ScoreBlock(
                label = stringResource(R.string.report_anxiety_before),
                score = data.beforeScore,
                illustrationRes = anxietyScoreIllustration(data.beforeScore),
                backgroundColor = when {
                    isFlat -> MaterialTheme.colorScheme.tertiary
                    isRising -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.primary300Token
                },
            )
            ScoreBlock(
                label = stringResource(R.string.report_anxiety_after),
                score = data.afterScore,
                illustrationRes = anxietyScoreIllustration(data.afterScore),
                backgroundColor = when {
                    isFlat -> MaterialTheme.colorScheme.tertiary
                    isRising -> MaterialTheme.colorScheme.primary300Token
                    else -> MaterialTheme.colorScheme.secondaryContainer
                },
            )
        }

        Column(
            modifier = Modifier.offset(x = 20.dp, y = 191.dp).size(15.dp, 160.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // 그래프의 세로축은 불안 점수 범위인 0~10을 나타냅니다.
            listOf("10", "8", "6", "4", "2", "0").forEach {
                Text(
                    text = it,
                    modifier = Modifier.size(15.dp, 20.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Canvas(
            modifier = Modifier.offset(x = 69.dp, y = 188.dp).size(213.dp, 156.dp),
        ) {
            // 점수가 높을수록 위쪽에 오도록 0~10 점수를 그래프 좌표로 변환합니다.
            fun scoreY(score: Double): Float =
                (153 - 14 * score.coerceIn(0.0, 10.0)).dp.toPx()

            val start = Offset(3.dp.toPx(), scoreY(data.beforeScore))
            val end = Offset(210.dp.toPx(), scoreY(data.afterScore))
            drawLine(graphColor, start, end, strokeWidth = 1.dp.toPx())
            drawCircle(graphColor, 3.dp.toPx(), start)
            drawCircle(graphColor, 3.dp.toPx(), end)
        }

        Canvas(
            modifier = Modifier.offset(x = 16.dp, y = 366.dp).size(303.dp, 1.dp),
        ) {
            drawLine(
                color = dividerColor,
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
                .padding(start = 16.dp, top = 378.dp, end = 16.dp, bottom = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            // 서버 피드백과 프론트 팁의 길이에 맞춰 카드 하단 높이가 확장됩니다.
            Text(
                text = data.feedback,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                text = stringResource(
                    if (data.state == AnxietyChangeState.DECREASED) {
                        R.string.report_anxiety_tip_decreased
                    } else {
                        R.string.report_anxiety_tip_default
                    },
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun ScoreBlock(
    label: String,
    score: Double,
    @DrawableRes illustrationRes: Int,
    backgroundColor: androidx.compose.ui.graphics.Color,
) {
    Row(
        modifier = Modifier
            .size(150.dp, 86.dp)
            .background(backgroundColor, MaterialTheme.shapes.large)
            .padding(start = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(White800, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(illustrationRes),
                contentDescription = stringResource(
                    R.string.report_anxiety_illustration_description,
                    label,
                ),
                modifier = Modifier.size(48.dp),
            )
        }
        Column(modifier = Modifier.width(62.dp)) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = score.displayScore(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineLarge,
                    maxLines = 1,
                )
                Text(
                    text = stringResource(R.string.report_score_scale),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                )
            }
        }
    }
}

@DrawableRes
internal fun anxietyScoreIllustration(score: Double): Int = when (
    score.coerceIn(0.0, 10.0).roundToInt()
) {
    0 -> R.drawable.worry_cat_0
    1, 2 -> R.drawable.worry_cat_1_2
    3, 4 -> R.drawable.worry_cat_3_4
    5, 6 -> R.drawable.worry_cat_5_6
    7, 8 -> R.drawable.worry_cat_7_8
    else -> R.drawable.worry_cat_9_10
}

@Composable
private fun BoxScope.AnxietyCardHeader(
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
                text = stringResource(R.string.report_tab_anxiety_gap),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.heading2Token,
                maxLines = 1,
            )
        }
        Text(
            text = stringResource(R.string.report_anxiety_subtitle),
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
