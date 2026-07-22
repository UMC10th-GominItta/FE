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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gominitta.android.ui.components.DateRangeOption
import com.gominitta.android.ui.components.DateSelectMenu
import com.gominitta.android.ui.components.HeartReportTab
import com.gominitta.android.ui.components.ReportCard
import com.gominitta.android.ui.components.WorryMapLargeBubble
import com.gominitta.android.ui.components.WorryMapMediumBubble
import com.gominitta.android.ui.components.WorryMapSmallBubble
import com.gominitta.android.ui.theme.AccentCream100
import com.gominitta.android.ui.theme.AccentCream300
import com.gominitta.android.ui.theme.Body2_15r
import com.gominitta.android.ui.theme.Body3_14r
import com.gominitta.android.ui.theme.Button1_15m
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Gray400
import com.gominitta.android.ui.theme.Gray600
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.Heading2_22m
import com.gominitta.android.ui.theme.Primary400

// =============================================================================
// 탭 진입점: 데이터 유무에 따라 표시할 카드를 선택
// =============================================================================

@Composable
internal fun WorryThemeMapTab(
    hasData: Boolean = false,
    modifier: Modifier = Modifier,
) {
    var selectedDateRange by rememberSaveable {
        mutableStateOf(DateRangeOption.LAST_30_DAYS)
    }
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
    ReportCard(
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
            color = Gray400,
            style = Body2_15r,
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
    // 데이터가 있을 때 사용하는 488dp 카드
    ReportCard(
        modifier = modifier,
        height = 488.dp,
    ) {
        WorryThemeMapCardHeader(
            selectedDateRange = selectedDateRange,
            onDateRangeSelected = onDateRangeSelected,
            initiallyExpanded = false,
        )

        // 1번 원: 진로 70%
        WorryMapLargeBubble(
            title = "진로",
            percentage = "${reportData.percentages[0]}%",
            modifier = Modifier.offset(x = 104.dp, y = 153.dp),
        )

        // 2번 원: 학업 40%
        WorryMapMediumBubble(
            title = "학업",
            percentage = "${reportData.percentages[1]}%",
            backgroundColor = Primary400,
            modifier = Modifier.offset(x = 206.dp, y = 86.dp),
        )

        // 3번 원: 학업 40%
        WorryMapMediumBubble(
            title = "학업",
            percentage = "${reportData.percentages[2]}%",
            backgroundColor = AccentCream300,
            modifier = Modifier.offset(x = 186.dp, y = 267.dp),
        )

        // 4번 원: 취업 40%
        WorryMapMediumBubble(
            title = "취업",
            percentage = "${reportData.percentages[3]}%",
            backgroundColor = AccentCream100,
            modifier = Modifier.offset(x = 16.dp, y = 217.dp),
        )

        // 5번 원: 돈 10%
        WorryMapSmallBubble(
            title = "돈",
            percentage = "${reportData.percentages[4]}%",
            modifier = Modifier.offset(x = 51.dp, y = 122.dp),
        )

        // 5번 원: 건강 10%
        WorryMapSmallBubble(
            title = "건강",
            percentage = "${reportData.percentages[5]}%",
            modifier = Modifier.offset(x = 108.dp, y = 289.dp),
        )

        // 5번 원: 가족 10%
        WorryMapSmallBubble(
            title = "가족",
            percentage = "${reportData.percentages[6]}%",
            modifier = Modifier.offset(x = 239.dp, y = 189.dp),
        )

        // 카드 하단 점선 구분선
        Canvas(
            modifier = Modifier
                .offset(x = 16.dp, y = 394.dp)
                .size(width = 303.dp, height = 1.dp),
        ) {
            drawLine(
                color = Gray400,
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
                color = Gray800,
                style = Button1_15m,
                maxLines = 1,
            )

            Text(
                text = "tip. 어떤 걱정이 자주 찾아오는지 아는 것만으로도,\n마음을 돌보는 첫걸음이 돼요.",
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
// 데이터 카드 하단 분석 문장의 강조 Span
// =============================================================================

private fun buildWorrySummaryText(): AnnotatedString = buildAnnotatedString {
    append("최근에는 ")
    pushStyle(
        SpanStyle(
            color = Gray800,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
        ),
    )
    append("진로와 가족")
    pop()
    append(" 관련된 걱정이 가장 많았어요.")
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
                color = Gray800,
                style = Heading2_22m,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }

        Text(
            text = "자주 예약한 걱정들의 키워드들이에요.",
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
