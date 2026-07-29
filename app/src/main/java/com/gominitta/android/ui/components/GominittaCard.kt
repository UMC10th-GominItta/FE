package com.gominitta.android.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gominitta.android.ui.theme.AccentCream100
import com.gominitta.android.ui.theme.Body2_15r
import com.gominitta.android.ui.theme.GominittaTheme
import com.gominitta.android.ui.theme.Primary200
import com.gominitta.android.ui.theme.Primary300
import com.gominitta.android.ui.theme.White800

/**
 * 테두리(1px) 카드 — 같은 형태에 **배경·테두리 색만 다른 3가지 변형**.
 * (예: 걱정 예약 날짜 카드) 색 조합은 [GominittaCardVariant] 에 모아 둔다.
 *
 * 홈 화면의 큰 그림자 카드는 형태가 달라 별도 컴포넌트([GominittaElevatedCard]) 사용.
 */
enum class GominittaCardVariant(val container: Color, val border: Color) {
    Type1(container = White800,       border = Primary200),         // #FEFEFB / #F3F0EB
    Type2(container = AccentCream100, border = Primary300),         // #FDF1D6 / #ECDFCE
    Type3(container = White800,       border = Primary300),         // #FEFEFB / #ECDFCE
}

@Composable
fun GominittaCard(
    modifier: Modifier = Modifier,
    variant: GominittaCardVariant = GominittaCardVariant.Type1,
    shape: Shape = RoundedCornerShape(12.dp),
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        color = variant.container,
        border = BorderStroke(1.dp, variant.border),
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }
}

// ---- Preview ---------------------------------------------------------------

@Preview(name = "GominittaCard variants", showBackground = true, backgroundColor = 0xFFFEFEFB)
@Composable
private fun GominittaCardPreview() {
    GominittaTheme {
        Column(Modifier.padding(16.dp)) {
            GominittaCardVariant.entries.forEach { v ->
                GominittaCard(variant = v, modifier = Modifier.padding(vertical = 6.dp)) {
                    Text("04/13 월요일", style = Body2_15r)
                    Text("10:00 AM 부터", style = Body2_15r)
                }
            }
        }
    }
}
