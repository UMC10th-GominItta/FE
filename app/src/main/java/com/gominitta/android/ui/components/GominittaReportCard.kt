package com.gominitta.android.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.reportCardToken

/**
 * 마음 리포트의 탭별 콘텐츠를 담는 공통 카드입니다.
 * 너비는 335dp로 고정합니다. [height]가 null이면 [minHeight]를 유지하면서
 * 내부 콘텐츠 높이에 맞춰 카드가 늘어납니다.
 */
@Composable
fun GominittaReportCard(
    modifier: Modifier = Modifier,
    height: Dp? = 488.dp,
    minHeight: Dp = 0.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = MaterialTheme.shapes.reportCardToken

    Surface(
        modifier = modifier
            .requiredWidth(335.dp)
            .then(
                if (height != null) Modifier.height(height) else Modifier.heightIn(min = minHeight),
            )
            .dropShadow(
                shape = shape,
                shadow = Shadow(
                    radius = 16.dp,
                    color = Color(0x0A000000),
                    offset = DpOffset(x = 0.dp, y = 3.dp),
                ),
            ),
        shape = shape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 0.dp,
    ) {
        Box(content = content)
    }
}

@Preview(
    name = "Report card",
    showBackground = true,
    backgroundColor = 0xFFF3F0EB,
    widthDp = 375,
    heightDp = 528,
)
@Composable
private fun ReportCardPreview() {
    GominittaTheme {
        GominittaReportCard(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
            content = {},
        )
    }
}
