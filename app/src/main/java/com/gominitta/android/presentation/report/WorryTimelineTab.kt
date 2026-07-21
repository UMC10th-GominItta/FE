package com.gominitta.android.presentation.report

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.gominitta.android.ui.theme.Button1_15m
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Gray400
import com.gominitta.android.ui.theme.Gray600
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.Heading2_22m

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

    // -------------------------------------------------------------------------
    // 메인 리포트 카드: 335 × 461dp
    // -------------------------------------------------------------------------
    ReportCard(
        modifier = modifier.padding(top = 28.dp),
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
                text = "목요일 저녁 시간대(18-24시)와\n일요일 밤 시간대(00-06시)에\n걱정 기록이 많았어요.",
                modifier = Modifier
                    .width(303.dp)
                    .height(63.dp),
                color = Gray800,
                style = Button1_15m,
            )

            Text(
                text = "tip. 마음이 자주 흔들리는 시간을 알면, 나에게 필요한 휴식 루틴도 더 잘 보일 수 있어요.",
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
        ) { selectedTab ->
            if (selectedTab == HeartReportTab.WORRY_TIMELINE) {
                WorryTimelineTab()
            }
        }
    }
}
