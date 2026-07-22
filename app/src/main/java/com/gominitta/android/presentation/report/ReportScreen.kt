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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
 * 실제 API 연결 전까지 [worryThemeHasData]로 테마 지도의 데이터 유무 화면을 전환합니다.
 */
@Composable
fun ReportScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    initialTab: HeartReportTab = HeartReportTab.WORRY_THEME_MAP,
    worryThemeHasData: Boolean = true,
) {
    var selectedTab by remember { mutableStateOf(initialTab) }
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialTab.ordinal,
    )
    val coroutineScope = rememberCoroutineScope()

    // 현재 뷰포트 안에서 노출 면적이 가장 큰 카드를 찾아 상단 탭 상태와 동기화합니다.
    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val viewportStart = layoutInfo.viewportStartOffset
            val viewportEnd = layoutInfo.viewportEndOffset

            layoutInfo.visibleItemsInfo
                .maxByOrNull { item ->
                    val visibleStart = maxOf(item.offset, viewportStart)
                    val visibleEnd = minOf(item.offset + item.size, viewportEnd)
                    (visibleEnd - visibleStart).coerceAtLeast(0)
                }
                ?.index
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
        // 상태바 아래에 고정되는 화면 제목 영역
        Spacer(Modifier.height(56.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .width(271.dp)
                    .height(28.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "마음 리포트",
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.heading3Token,
                    textAlign = TextAlign.Center,
                )
            }
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
                WorryThemeMapTab(hasData = worryThemeHasData)
            }
            item(key = HeartReportTab.ANXIETY_TEMPERATURE) {
                AnxietyTemperatureTab()
            }
            item(key = HeartReportTab.WORRY_TIMELINE) {
                WorryTimelineTab()
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
        ReportScreen(onNavigateBack = {})
    }
}
