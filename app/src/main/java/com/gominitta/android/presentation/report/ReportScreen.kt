package com.gominitta.android.presentation.report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gominitta.android.ui.components.HeartReportButton
import com.gominitta.android.ui.components.HeartReportTab
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.Heading3_20m

/**
 * 마음 리포트의 공통 화면 골격입니다.
 * 하단 메뉴바는 MainScreen에서 제공하며, 탭과 탭별 화면은 [content]에서 조합합니다.
 */
@Composable
fun ReportScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(HeartReportTab) -> Unit = {},
) {
    var selectedTab by remember { mutableStateOf(HeartReportTab.WORRY_THEME_MAP) }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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
                    color = Gray800,
                    style = Heading3_20m,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        HeartReportButton(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
        )

        content(selectedTab)
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
