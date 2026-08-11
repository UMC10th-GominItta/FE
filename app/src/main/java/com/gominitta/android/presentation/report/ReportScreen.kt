package com.gominitta.android.presentation.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.gominitta.android.R
import com.gominitta.android.ui.components.DateRangeOption
import com.gominitta.android.ui.components.GominittaHeartReportButton
import com.gominitta.android.ui.components.HeartReportTab
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.heading3Token
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * 마음 리포트의 진입 화면입니다.
 *
 * 상단 제목과 리포트 탭 바는 고정하고, 아래 [LazyColumn]에 세 리포트 카드를
 * 걱정 테마 지도 → 불안 온도차 → 걱정 타임라인 순서로 배치합니다.
 * 탭을 누르면 해당 카드로 이동하며, 사용자가 직접 스크롤할 때는 화면에 가장 많이
 * 노출된 카드에 맞춰 탭의 Active 상태를 갱신합니다.
 *
 * 화면 상태와 기간 변경 이벤트는 [ReportViewModel]을 사용하는 [ReportRoute]에서 전달합니다.
 */
@Composable
fun ReportRoute(
    modifier: Modifier = Modifier,
    initialTab: HeartReportTab = HeartReportTab.WORRY_THEME_MAP,
    viewModel: ReportViewModel = hiltViewModel(),
) {
    // ViewModel 상태를 수명주기에 맞춰 구독하고 화면 이벤트를 다시 ViewModel에 전달합니다.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ReportScreen(
        uiState = uiState,
        onWorryThemeRangeSelected = viewModel::selectWorryThemeRange,
        onAnxietyRangeSelected = viewModel::selectAnxietyRange,
        onTimelineRangeSelected = viewModel::selectTimelineRange,
        modifier = modifier,
        initialTab = initialTab,
    )
}

@Composable
fun ReportScreen(
    uiState: ReportUiState,
    onWorryThemeRangeSelected: (DateRangeOption) -> Unit,
    onAnxietyRangeSelected: (DateRangeOption) -> Unit,
    onTimelineRangeSelected: (DateRangeOption) -> Unit,
    modifier: Modifier = Modifier,
    initialTab: HeartReportTab = HeartReportTab.WORRY_THEME_MAP,
) {
    // 선택 탭과 카드 목록의 스크롤 위치를 함께 관리합니다.
    var selectedTab by remember { mutableStateOf(initialTab) }
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialTab.ordinal,
    )
    val coroutineScope = rememberCoroutineScope()
    // 현재 뷰포트 안에서 노출 면적이 가장 큰 카드를 찾아 상단 탭 상태와 동기화합니다.
    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo.mostVisibleItemIndex()
        }
            .distinctUntilChanged()
            .collect { visibleIndex ->
                visibleIndex?.let { index ->
                    HeartReportTab.entries.getOrNull(index)?.let { tab ->
                        selectedTab = tab
                    }
                }
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 시스템 바 인셋은 MainScreen의 Scaffold가 적용한다 (마음세션/마음레시피 탭과 동일).
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.report_title),
                modifier = Modifier.width(271.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.heading3Token,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(16.dp))

        // 탭 클릭 시 enum 순서와 동일한 LazyColumn item 위치로 앵커 스크롤합니다.
        GominittaHeartReportButton(
            selectedTab = selectedTab,
            onTabSelected = { tab ->
                selectedTab = tab
                coroutineScope.launch {
                    listState.animateScrollToItem(tab.ordinal)
                }
            },
        )

        // 세 리포트 카드가 실제로 스크롤되는 영역입니다. 상단 탭 바는 이 영역 밖에 있습니다.
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = listState,
            contentPadding = PaddingValues(top = 28.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item(key = HeartReportTab.WORRY_THEME_MAP) {
                WorryThemeMapTab(
                    selectedRange = uiState.worryThemeRange,
                    data = uiState.worryThemeData,
                    onRangeSelected = onWorryThemeRangeSelected,
                )
            }
            item(key = HeartReportTab.ANXIETY_TEMPERATURE) {
                AnxietyTemperatureTab(
                    selectedRange = uiState.anxietyRange,
                    data = uiState.anxietyData,
                    onRangeSelected = onAnxietyRangeSelected,
                )
            }
            item(key = HeartReportTab.WORRY_TIMELINE) {
                WorryTimelineTab(
                    selectedRange = uiState.timelineRange,
                    data = uiState.timelineData,
                    onRangeSelected = onTimelineRangeSelected,
                )
            }
        }
    }
}

@Preview(
    name = "Report screen - common layout",
    showBackground = true,
    backgroundColor = 0xFFFEFEFB,
    widthDp = 375,
    heightDp = 812,
)
@Composable
private fun ReportScreenPreview() {
    GominittaTheme {
        val range = DateRangeOption.LAST_30_DAYS
        ReportScreen(
            uiState = ReportUiState(
                worryThemeRange = range,
                worryThemeData = worryThemeDummyData(range),
                anxietyRange = range,
                anxietyData = anxietyDummyData(range),
                timelineRange = range,
                timelineData = worryTimelineDummyData(range),
            ),
            onWorryThemeRangeSelected = {},
            onAnxietyRangeSelected = {},
            onTimelineRangeSelected = {},
        )
    }
}

/** 현재 뷰포트에서 실제로 노출된 세로 길이가 가장 큰 카드의 목록 인덱스를 반환합니다. */
private fun LazyListLayoutInfo.mostVisibleItemIndex(): Int? {
    val viewportStart = viewportStartOffset
    val viewportEnd = viewportEndOffset

    return visibleItemsInfo
        .maxByOrNull { item ->
            val visibleStart = maxOf(item.offset, viewportStart)
            val visibleEnd = minOf(item.offset + item.size, viewportEnd)
            (visibleEnd - visibleStart).coerceAtLeast(0)
        }
        ?.index
}
