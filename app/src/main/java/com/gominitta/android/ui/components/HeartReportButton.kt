package com.gominitta.android.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gominitta.android.ui.theme.AccentCream100
import com.gominitta.android.ui.theme.Button1_15m
import com.gominitta.android.ui.theme.Gray800
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Primary200
import com.gominitta.android.ui.theme.Primary400
import com.gominitta.android.ui.theme.White800

enum class HeartReportTab(
    val title: String,
    internal val width: Dp,
) {
    WORRY_THEME_MAP("걱정 테마 지도", 116.dp),
    ANXIETY_TEMPERATURE("불안 온도차", 99.dp),
    WORRY_TIMELINE("걱정 타임라인", 112.dp),
}

/**
 * 마음 리포트의 세 가지 내용을 전환하는 탭 버튼입니다.
 */
@Composable
fun HeartReportButton(
    selectedTab: HeartReportTab,
    onTabSelected: (HeartReportTab) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        HeartReportTab.entries.forEach { tab ->
            HeartReportTabButton(
                tab = tab,
                selected = tab == selectedTab,
                enabled = enabled,
                onClick = { onTabSelected(tab) },
            )
        }
    }
}

@Composable
private fun HeartReportTabButton(
    tab: HeartReportTab,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    val backgroundColor = if (selected) AccentCream100 else White800
    val borderColor = if (selected) Primary400 else Primary200

    Surface(
        modifier = Modifier
            .width(tab.width)
            .height(37.dp)
            .clickable(
                enabled = enabled,
                role = Role.Tab,
                onClick = onClick,
            ),
        shape = shape,
        color = backgroundColor,
        contentColor = Gray800,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = 0.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = tab.title,
                color = if (enabled) Gray800 else Gray800.copy(alpha = 0.38f),
                style = Button1_15m,
                maxLines = 1,
            )
        }
    }
}

@Preview(
    name = "Heart report tabs",
    showBackground = true,
    backgroundColor = 0xFFFEFEFB,
    widthDp = 350,
)
@Composable
private fun HeartReportButtonPreview() {
    GominittaTheme {
        var selectedTab by remember { mutableStateOf(HeartReportTab.WORRY_THEME_MAP) }

        HeartReportButton(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier.padding(8.dp),
        )
    }
}

@Preview(
    name = "Heart report tabs - Anxiety temperature",
    showBackground = true,
    backgroundColor = 0xFFFEFEFB,
    widthDp = 350,
)
@Composable
private fun HeartReportButtonAnxietyTemperaturePreview() {
    GominittaTheme {
        HeartReportButton(
            selectedTab = HeartReportTab.ANXIETY_TEMPERATURE,
            onTabSelected = {},
            modifier = Modifier.padding(8.dp),
        )
    }
}

@Preview(
    name = "Heart report tabs - Worry timeline",
    showBackground = true,
    backgroundColor = 0xFFFEFEFB,
    widthDp = 350,
)
@Composable
private fun HeartReportButtonWorryTimelinePreview() {
    GominittaTheme {
        HeartReportButton(
            selectedTab = HeartReportTab.WORRY_TIMELINE,
            onTabSelected = {},
            modifier = Modifier.padding(8.dp),
        )
    }
}
