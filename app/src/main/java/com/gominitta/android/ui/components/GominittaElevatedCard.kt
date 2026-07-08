package com.gominitta.android.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gominitta.android.ui.theme.AccentCream200
import com.gominitta.android.ui.theme.Body2_15r
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Heading1_24sb

/**
 * 그림자 카드 — 홈 화면의 큰 콘텐츠 카드(히어로·섹션)용. 테두리 없이 부드러운
 * **피치 그림자(#FBEACB)**, 큰 라운드(24dp)의 순수 컨테이너. 내용은 [content] 슬롯으로 받는다.
 *
 * 테두리형 카드는 [GominittaCard] 참고.
 */
@Composable
fun GominittaElevatedCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    contentPadding: PaddingValues = PaddingValues(20.dp),
    decoration: @Composable (BoxScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = AccentCream200,   // #FBEACB tinted shadow
                spotColor = AccentCream200,      // (colored shadow: API 28+; black fallback below)
            ),
        shape = shape,
        color = MaterialTheme.colorScheme.surface,   // White800
    ) {
        Box {
            // 장식(잎 일러스트 등)은 카드 크기에 영향 없이 뒤에 깔고, Surface 라운드로 클립됨.
            if (decoration != null) {
                Box(Modifier.matchParentSize()) { decoration() }
            }
            Column(modifier = Modifier.padding(contentPadding), content = content)
        }
    }
}

// ---- Preview ---------------------------------------------------------------

@Preview(name = "GominittaElevatedCard", showBackground = true, backgroundColor = 0xFFF3F0EB)
@Composable
private fun GominittaElevatedCardPreview() {
    GominittaTheme {
        GominittaElevatedCard(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "걱정은 잠시 접어두고\n이따가 마주해요.",
                style = Heading1_24sb,
            )
            Text(
                text = "오늘은 어떤 생각이 드나요?",
                style = Body2_15r,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
