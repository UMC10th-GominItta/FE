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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
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
import com.gominitta.android.ui.theme.GominittaTheme

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
fun GominittaHeartReportButton(
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
    val shape = MaterialTheme.shapes.medium
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.surface
    }
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.outline
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

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
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = 0.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = tab.title,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                },
                style = MaterialTheme.typography.labelLarge,
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

        GominittaHeartReportButton(
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
        GominittaHeartReportButton(
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
        GominittaHeartReportButton(
            selectedTab = HeartReportTab.WORRY_TIMELINE,
            onTabSelected = {},
            modifier = Modifier.padding(8.dp),
        )
    }
}
