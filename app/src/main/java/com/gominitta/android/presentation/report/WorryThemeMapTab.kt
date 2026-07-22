package com.gominitta.android.presentation.report

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gominitta.android.ui.components.DateRangeOption
import com.gominitta.android.ui.components.GominittaDateSelectMenu
import com.gominitta.android.ui.components.HeartReportTab
import com.gominitta.android.ui.components.GominittaReportCard
import com.gominitta.android.ui.components.GominittaWorryMapBubble
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.gray400Token
import com.gominitta.android.ui.theme.heading2Token

// =============================================================================
// 탭 진입점: 데이터 유무에 따라 표시할 카드를 선택
// =============================================================================

/**
 * 자주 기록된 걱정 키워드를 버블 차트로 보여주는 탭입니다.
 * 기간 변경 시 더미 데이터의 비율과 하단 분석 문구가 갱신됩니다.
 * [hasData]는 API 연동 전 데이터 있음/없음 디자인을 확인하기 위한 임시 상태입니다.
 */
@Composable
internal fun WorryThemeMapTab(
    hasData: Boolean = false,
    modifier: Modifier = Modifier,
) {
    var selectedDateRange by rememberSaveable {
        mutableStateOf(DateRangeOption.LAST_30_DAYS)
    }
    // 기간별 버블 비율과 분석 문구를 함께 갱신합니다.
    val reportData = worryThemeDummyData(selectedDateRange)

    if (hasData) {
        WorryThemeMapDataCard(
            selectedDateRange = selectedDateRange,
            onDateRangeSelected = { selectedDateRange = it },
            reportData = reportData,
            modifier = modifier,
        )
    } else {
        WorryThemeMapEmptyCard(
            selectedDateRange = selectedDateRange,
            onDateRangeSelected = { selectedDateRange = it },
            modifier = modifier,
        )
    }
}

// =============================================================================
// 데이터 없음 카드: 453dp / 펼친 기간 메뉴 / 중앙 안내 문구
// =============================================================================

@Composable
private fun WorryThemeMapEmptyCard(
    selectedDateRange: DateRangeOption,
    onDateRangeSelected: (DateRangeOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    // 데이터가 없을 때 사용하는 453dp 카드
    GominittaReportCard(
        modifier = modifier,
        height = 453.dp,
    ) {
        WorryThemeMapCardHeader(
            selectedDateRange = selectedDateRange,
            onDateRangeSelected = onDateRangeSelected,
            initiallyExpanded = true,
        )

        // 데이터가 없을 때 카드 중앙에 표시되는 안내 문구
        Text(
            text = "아직 리포트를 분석하기에\n걱정 기록이 조금 부족해요.\n\n세션을 조금 더 진행해 볼까요?",
            modifier = Modifier
                .offset(x = 81.dp, y = 190.dp)
                .size(width = 174.dp, height = 84.dp),
            color = MaterialTheme.colorScheme.gray400Token,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

// =============================================================================
// 데이터 있음 카드: 488dp / 닫힌 기간 메뉴 / 버블 지도 / 분석 결과
// =============================================================================

@Composable
private fun WorryThemeMapDataCard(
    selectedDateRange: DateRangeOption,
    onDateRangeSelected: (DateRangeOption) -> Unit,
    reportData: WorryThemeReportData,
    modifier: Modifier = Modifier,
) {
    val dividerColor = MaterialTheme.colorScheme.gray400Token

    // 데이터가 있을 때 사용하는 488dp 카드
    GominittaReportCard(
        modifier = modifier,
        height = 488.dp,
    ) {
        WorryThemeMapCardHeader(
            selectedDateRange = selectedDateRange,
            onDateRangeSelected = onDateRangeSelected,
            initiallyExpanded = false,
        )

        // 버블은 비율에 따라 대(128dp)·중(92dp)·소(64dp) 크기를 선택합니다.
        // 위치는 디자인 좌표에 고정되며 자동 패킹이나 물리 애니메이션은 적용하지 않습니다.
        // 1번 원: 진로
        GominittaWorryMapBubble(
            title = "진로",
            value = reportData.percentages[0],
            modifier = Modifier.offset(x = 104.dp, y = 153.dp),
        )

        // 2번 원: 학업 40%
        GominittaWorryMapBubble(
            title = "학업",
            value = reportData.percentages[1],
            mediumBackgroundColor = MaterialTheme.colorScheme.outline,
            modifier = Modifier.offset(x = 206.dp, y = 86.dp),
        )

        // 3번 원: 학업 40%
        GominittaWorryMapBubble(
            title = "학업",
            value = reportData.percentages[2],
            mediumBackgroundColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.offset(x = 186.dp, y = 267.dp),
        )

        // 4번 원: 취업 40%
        GominittaWorryMapBubble(
            title = "취업",
            value = reportData.percentages[3],
            mediumBackgroundColor = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.offset(x = 16.dp, y = 217.dp),
        )

        // 5번 원: 돈 10%
        GominittaWorryMapBubble(
            title = "돈",
            value = reportData.percentages[4],
            modifier = Modifier.offset(x = 51.dp, y = 122.dp),
        )

        // 5번 원: 건강 10%
        GominittaWorryMapBubble(
            title = "건강",
            value = reportData.percentages[5],
            modifier = Modifier.offset(x = 108.dp, y = 289.dp),
        )

        // 5번 원: 가족 10%
        GominittaWorryMapBubble(
            title = "가족",
            value = reportData.percentages[6],
            modifier = Modifier.offset(x = 239.dp, y = 189.dp),
        )

        // 카드 하단 점선 구분선
        Canvas(
            modifier = Modifier
                .offset(x = 16.dp, y = 394.dp)
                .size(width = 303.dp, height = 1.dp),
        ) {
            drawLine(
                color = dividerColor,
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                strokeWidth = 0.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(
                    intervals = floatArrayOf(4.dp.toPx(), 4.dp.toPx()),
                ),
            )
        }

        // 카드 하단 분석 요약 및 도움말
        Column(
            modifier = Modifier
                .offset(x = 16.dp, y = 406.dp)
                .size(width = 303.dp, height = 66.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = reportData.summary,
                modifier = Modifier
                    .width(303.dp)
                    .height(22.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
            )

            Text(
                text = "tip. 어떤 걱정이 자주 찾아오는지 아는 것만으로도,\n마음을 돌보는 첫걸음이 돼요.",
                modifier = Modifier
                    .width(303.dp)
                    .height(40.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

// =============================================================================
// 두 카드가 공통으로 사용하는 제목·설명·기간 선택 메뉴
// =============================================================================

@Composable
private fun BoxScope.WorryThemeMapCardHeader(
    selectedDateRange: DateRangeOption,
    onDateRangeSelected: (DateRangeOption) -> Unit,
    initiallyExpanded: Boolean,
) {
    // 두 카드가 공통으로 사용하는 상단 제목 및 설명 영역
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
                text = "걱정 테마 지도",
                modifier = Modifier
                    .width(122.dp)
                    .height(31.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.heading2Token,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }

        Text(
            text = "자주 예약한 걱정들의 키워드들이에요.",
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
        onOptionSelected = onDateRangeSelected,
        modifier = Modifier.offset(x = 223.dp, y = 16.dp),
        initiallyExpanded = initiallyExpanded,
    )
}

// =============================================================================
// 상태별 전체 화면 Preview
// =============================================================================

@Preview(
    name = "Worry theme map - empty",
    showBackground = true,
    backgroundColor = 0xFFFEFEFB,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun WorryThemeMapEmptyPreview() {
    GominittaTheme {
        ReportScreen(
            onNavigateBack = {},
            worryThemeHasData = false,
        )
    }
}

@Preview(
    name = "Worry theme map - data",
    showBackground = true,
    backgroundColor = 0xFFFEFEFB,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun WorryThemeMapDataPreview() {
    GominittaTheme {
        ReportScreen(
            onNavigateBack = {},
            worryThemeHasData = true,
        )
    }
}
