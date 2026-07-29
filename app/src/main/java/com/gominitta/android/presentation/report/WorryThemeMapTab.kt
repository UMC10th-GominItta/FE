package com.gominitta.android.presentation.report

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.gominitta.android.ui.components.GominittaReportCard
import com.gominitta.android.ui.components.GominittaWorryMapBubble
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.gray400Token
import com.gominitta.android.ui.theme.heading2Token

@Composable
internal fun WorryThemeMapTab(
    dataProvider: (DateRangeOption) -> WorryThemeReportData? = ::worryThemeDummyData,
    onDateRangeChanged: (DateRangeOption) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var selectedRange by rememberSaveable { mutableStateOf(DateRangeOption.LAST_30_DAYS) }
    val data = dataProvider(selectedRange)
    val selectRange: (DateRangeOption) -> Unit = {
        selectedRange = it
        onDateRangeChanged(it)
    }

    if (data?.canRender == true) {
        WorryThemeMapDataCard(selectedRange, selectRange, data, modifier)
    } else {
        WorryThemeMapEmptyCard(selectedRange, selectRange, modifier)
    }
}

@Composable
private fun WorryThemeMapEmptyCard(
    selectedRange: DateRangeOption,
    onRangeSelected: (DateRangeOption) -> Unit,
    modifier: Modifier,
) {
    GominittaReportCard(modifier = modifier, height = 453.dp) {
        WorryThemeMapCardHeader(selectedRange, onRangeSelected)
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
private fun WorryThemeMapDataCard(
    selectedRange: DateRangeOption,
    onRangeSelected: (DateRangeOption) -> Unit,
    data: WorryThemeReportData,
    modifier: Modifier,
) {
    val rankedThemes = data.rankedThemes()
    val bubblePlacements = remember(rankedThemes) {
        layoutWorryThemeBubbles(rankedThemes)
    }
    val mediumColors = listOf(
        MaterialTheme.colorScheme.outline,
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
    )
    val dividerColor = MaterialTheme.colorScheme.gray400Token

    GominittaReportCard(modifier = modifier, height = 488.dp) {
        WorryThemeMapCardHeader(selectedRange, onRangeSelected)

        rankedThemes.zip(bubblePlacements).forEachIndexed { index, (ranked, placement) ->
            GominittaWorryMapBubble(
                title = ranked.item.theme.label,
                value = ranked.item.percentage,
                isPrimary = ranked.weight == WorryThemeWeight.PRIMARY,
                mediumBackgroundColor = mediumColors[index % mediumColors.size],
                modifier = Modifier.offset(
                    x = (8f + placement.x).dp,
                    y = (80f + placement.y).dp,
                ),
            )
        }

        Canvas(
            modifier = Modifier
                .offset(x = 16.dp, y = 394.dp)
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

        Column(
            modifier = Modifier
                .offset(x = 16.dp, y = 406.dp)
                .size(width = 303.dp, height = 66.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = data.feedbackText(),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
            )
            Text(
                text = "tip. 어떤 걱정이 자주 찾아오는지 아는 것만으로도,\n마음을 돌보는 첫걸음이 될 수 있어요.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun BoxScope.WorryThemeMapCardHeader(
    selectedRange: DateRangeOption,
    onRangeSelected: (DateRangeOption) -> Unit,
) {
    Column(
        modifier = Modifier
            .offset(x = 16.dp, y = 16.dp)
            .size(width = 303.dp, height = 53.dp),
    ) {
        Box(
            modifier = Modifier.size(width = 303.dp, height = 32.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = "걱정 테마 지도",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.heading2Token,
                maxLines = 1,
            )
        }
        Text(
            text = "요즘 예약한 걱정들의 키워드들이에요",
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

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun WorryThemeMapPreview() {
    GominittaTheme { WorryThemeMapTab() }
}
