package com.gominitta.android.presentation.report

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gominitta.android.ui.components.DateRangeOption
import com.gominitta.android.ui.components.GominittaDateSelectMenu
import com.gominitta.android.ui.components.GominittaReportCard
import com.gominitta.android.ui.components.moodCatDrawableRes
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.White800
import com.gominitta.android.ui.theme.gray400Token
import com.gominitta.android.ui.theme.heading2Token
import com.gominitta.android.ui.theme.primary300Token
import kotlin.math.roundToInt

@Composable
internal fun AnxietyTemperatureTab(
    selectedRange: DateRangeOption = DateRangeOption.LAST_30_DAYS,
    data: AnxietyReportData? = anxietyDummyData(selectedRange),
    onRangeSelected: (DateRangeOption) -> Unit = {},
    modifier: Modifier = Modifier,
) {
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
            text = "아직 리포트를 분석하기에 걱정 기록이 조금 부족해요.\n세션을 조금 더 진행해 볼까요?",
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
    val isRising = data.state == AnxietyChangeState.INCREASED
    val isFlat = data.state == AnxietyChangeState.MAINTAINED
    val graphColor = MaterialTheme.colorScheme.onSurface
    val dividerColor = MaterialTheme.colorScheme.gray400Token

    GominittaReportCard(modifier = modifier, height = 479.dp) {
        AnxietyCardHeader(selectedRange, onRangeSelected)

        Row(
            modifier = Modifier.offset(x = 16.dp, y = 85.dp).size(303.dp, 86.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ScoreBlock(
                label = "예약 시",
                score = data.beforeAverage,
                backgroundColor = when {
                    isFlat -> MaterialTheme.colorScheme.tertiary
                    isRising -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.primary300Token
                },
            )
            ScoreBlock(
                label = "세션 후",
                score = data.afterAverage,
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
            fun scoreY(score: Double): Float =
                (153 - 14 * score.coerceIn(0.0, 10.0)).dp.toPx()

            val start = Offset(3.dp.toPx(), scoreY(data.beforeAverage))
            val end = Offset(210.dp.toPx(), scoreY(data.afterAverage))
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
            modifier = Modifier.offset(x = 16.dp, y = 378.dp).size(303.dp, 85.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = data.summaryText(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
            )
            Text(
                text = data.tipText(),
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
            modifier = Modifier.size(48.dp).clip(CircleShape).background(White800),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(moodCatDrawableRes(score.roundToInt())),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Fit,
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
                    text = "/ 10",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                )
            }
        }
    }
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
                text = "불안 온도차",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.heading2Token,
                maxLines = 1,
            )
        }
        Text(
            text = "걱정 예약 시와 마음 세션 후 변화에요.",
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
private fun AnxietyTemperaturePreview() {
    GominittaTheme { AnxietyTemperatureTab() }
}
